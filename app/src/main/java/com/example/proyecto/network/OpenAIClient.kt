package com.example.proyecto.network

import com.example.proyecto.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Cliente para la Responses API de OpenAI.
 *
 * MODO RAG (el que pide la orden de trabajo): se envía únicamente el ID del
 * Vector Store del equipo detectado y la herramienta "file_search" busca los
 * fragmentos relevantes del lado de OpenAI. Desde Android NO se envía ningún
 * documento completo: solo la pregunta y el ID.
 *
 * MODO RESPALDO: si todavía no hay Vector Store configurado, se responde usando
 * como único contexto la ficha técnica interna de la app, y la respuesta se
 * marca explícitamente como tal. Sirve para no dejar el asistente muerto
 * mientras se suben los manuales, pero NO cumple el requisito de RAG.
 *
 * Requiere BuildConfig.OPENAI_API_KEY (definido en local.properties -> gradle).
 */
object OpenAIClient {

    private const val RESPONSES_URL = "https://api.openai.com/v1/responses"

    /**
     * Modelo a usar. Si el chat responde con "Error de la API (400)" mencionando
     * el modelo, cámbialo por uno que exista en tu cuenta (por ejemplo "gpt-4o").
     */
    private const val MODEL = "gpt-4o"

    /**
     * Vector Store común a todo el laboratorio. Se usa cuando el equipo no tiene
     * uno propio en EquipoRepository. Déjalo vacío si no lo vas a usar.
     */
    const val VECTOR_STORE_GENERAL = "vs_6aa28fa422488191a420010526a5fa36"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    interface RespuestaCallback {
        fun onSuccess(respuesta: String)
        fun onError(mensaje: String)
    }

    /**
     * @param vectorStoreId  Vector Store del equipo. Si viene vacío se intenta
     *                       [VECTOR_STORE_GENERAL] y, si tampoco hay, se cae al
     *                       modo respaldo con [contextoRespaldo].
     * @param contextoRespaldo Ficha técnica interna, en texto plano. Solo se usa
     *                       en modo respaldo.
     */
    fun preguntar(
        pregunta: String,
        vectorStoreId: String,
        instrucciones: String,
        contextoRespaldo: String = "",
        callback: RespuestaCallback
    ) {
        val apiKey = BuildConfig.OPENAI_API_KEY
        if (apiKey.isBlank()) {
            callback.onError("Falta configurar OPENAI_API_KEY en local.properties")
            return
        }

        val storeId = vectorStoreId.ifBlank { VECTOR_STORE_GENERAL }
        val usaRag = storeId.isNotBlank()

        if (!usaRag && contextoRespaldo.isBlank()) {
            callback.onError(
                "Este equipo no tiene base de conocimiento asignada. Sube los manuales a un " +
                    "Vector Store de OpenAI y copia su ID (vs_...) en EquipoRepository."
            )
            return
        }

        val cuerpo = JSONObject()
            .put("model", MODEL)

        if (usaRag) {
            val tool = JSONObject()
                .put("type", "file_search")
                .put("vector_store_ids", JSONArray().put(storeId))
            cuerpo.put("tools", JSONArray().put(tool))
            cuerpo.put("instructions", instrucciones)
            cuerpo.put("input", pregunta)
            // Pide que devuelva los fragmentos recuperados para poder citar la fuente.
            cuerpo.put("include", JSONArray().put("file_search_call.results"))
        } else {
            cuerpo.put(
                "instructions",
                instrucciones + "\n\nNO tienes documentos del laboratorio disponibles. " +
                    "Responde apoyándote ÚNICAMENTE en la ficha técnica interna que viene a " +
                    "continuación, y empieza tu respuesta con la línea exacta:\n" +
                    "\"⚠ Sin manuales del laboratorio cargados — respuesta basada en la ficha " +
                    "técnica interna de la app.\"\n" +
                    "Si la ficha no contiene lo que se pregunta, dilo y recomienda consultar al " +
                    "docente o responsable del laboratorio.\n\n" +
                    "=== FICHA TÉCNICA INTERNA ===\n" + contextoRespaldo
            )
            cuerpo.put("input", pregunta)
        }

        val request = Request.Builder()
            .url(RESPONSES_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(cuerpo.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    val cuerpoTexto = resp.body?.string().orEmpty()
                    if (!resp.isSuccessful) {
                        callback.onError("Error de la API (${resp.code}): $cuerpoTexto")
                        return
                    }
                    try {
                        val root = JSONObject(cuerpoTexto)
                        val texto = extraerTexto(root)
                        val fuentes = extraerFuentes(root)
                        callback.onSuccess(
                            if (fuentes.isEmpty()) texto
                            else texto + "\n\n📄 Fuentes: " + fuentes.joinToString(", ")
                        )
                    } catch (e: Exception) {
                        callback.onError("No se pudo interpretar la respuesta: ${e.message}")
                    }
                }
            }
        })
    }

    /** Extrae el texto de respuesta del formato de la Responses API de OpenAI. */
    private fun extraerTexto(root: JSONObject): String {
        if (root.has("output_text")) return root.getString("output_text")

        val output = root.optJSONArray("output") ?: return "El asistente no devolvió una respuesta de texto"
        val sb = StringBuilder()
        for (i in 0 until output.length()) {
            val item = output.getJSONObject(i)
            val content = item.optJSONArray("content") ?: continue
            for (j in 0 until content.length()) {
                val c = content.getJSONObject(j)
                if (c.has("text")) sb.append(c.getString("text"))
            }
        }
        return if (sb.isEmpty()) "El asistente no devolvió una respuesta de texto" else sb.toString()
    }

    /**
     * Recorre las anotaciones de la respuesta y devuelve los nombres de archivo
     * citados, sin repetir. Es lo que exige la orden: mostrar la fuente consultada.
     */
    private fun extraerFuentes(root: JSONObject): List<String> {
        val fuentes = linkedSetOf<String>()
        val output = root.optJSONArray("output") ?: return emptyList()
        for (i in 0 until output.length()) {
            val content = output.getJSONObject(i).optJSONArray("content") ?: continue
            for (j in 0 until content.length()) {
                val anotaciones = content.getJSONObject(j).optJSONArray("annotations") ?: continue
                for (k in 0 until anotaciones.length()) {
                    val a = anotaciones.getJSONObject(k)
                    val nombre = a.optString("filename").ifBlank { a.optString("file_id") }
                    if (nombre.isNotBlank()) fuentes.add(nombre)
                }
            }
        }
        return fuentes.toList()
    }
}
