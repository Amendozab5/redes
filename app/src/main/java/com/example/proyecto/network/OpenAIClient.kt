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
 * Cliente para la Responses API de OpenAI usando la tool "file_search" (RAG):
 * en vez de mandar los manuales completos desde la app, solo se manda el ID del
 * Vector Store correspondiente al equipo detectado y OpenAI busca los fragmentos
 * relevantes por su lado antes de responder.
 *
 * Requiere BuildConfig.OPENAI_API_KEY (definido en local.properties -> gradle).
 */
object OpenAIClient {

    private const val RESPONSES_URL = "https://api.openai.com/v1/responses"
    private const val MODEL = "gpt-5.5"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    interface RespuestaCallback {
        fun onSuccess(respuesta: String)
        fun onError(mensaje: String)
    }

    fun preguntar(
        pregunta: String,
        vectorStoreId: String,
        instrucciones: String,
        callback: RespuestaCallback
    ) {
        val apiKey = BuildConfig.OPENAI_API_KEY
        if (apiKey.isBlank()) {
            callback.onError("Falta configurar OPENAI_API_KEY en local.properties")
            return
        }
        if (vectorStoreId.isBlank()) {
            callback.onError("Este equipo todavía no tiene una base de conocimiento (Vector Store) asignada")
            return
        }

        val tool = JSONObject()
            .put("type", "file_search")
            .put("vector_store_ids", JSONArray().put(vectorStoreId))

        val cuerpo = JSONObject()
            .put("model", MODEL)
            .put("instructions", instrucciones)
            .put("input", pregunta)
            .put("tools", JSONArray().put(tool))

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
                        callback.onSuccess(extraerTexto(cuerpoTexto))
                    } catch (e: Exception) {
                        callback.onError("No se pudo interpretar la respuesta: ${e.message}")
                    }
                }
            }
        })
    }

    /** Extrae el texto de respuesta del formato de la Responses API de OpenAI. */
    private fun extraerTexto(json: String): String {
        val root = JSONObject(json)
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
}
