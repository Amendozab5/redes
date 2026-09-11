package com.example.proyecto.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.data.Equipo
import com.example.proyecto.data.EquipoRepository
import com.example.proyecto.databinding.ActivityChatBinding
import com.example.proyecto.network.OpenAIClient
import java.util.Locale

/**
 * Chat con el asistente inteligente (LLM + RAG) sobre un equipo específico.
 *
 * INTERACCIÓN POR VOZ:
 * - Entrada: [SpeechRecognizer] del sistema Android convierte la voz del usuario
 *   a texto y ese texto entra al mismo flujo RAG que el teclado.
 * - Salida: [TextToSpeech] lee la respuesta en voz alta.
 * Ambos son servicios nativos del dispositivo: no se envía audio a OpenAI y no
 * generan costo adicional. Solo viaja al servidor la pregunta en texto y el ID
 * del Vector Store.
 *
 * La respuesta se basa en los documentos subidos al Vector Store del equipo
 * (EquipoRepository.vectorStoreId u OpenAIClient.VECTOR_STORE_GENERAL). Si no
 * hay ninguno configurado, se usa la ficha técnica interna como respaldo y la
 * respuesta sale marcada como tal.
 */
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLASE_EQUIPO = "extra_clase_equipo"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private val mensajes = mutableListOf<ChatMessage>()

    private var equipoActual: Equipo? = null

    private var reconocedor: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var ttsListo = false
    private var escuchando = false

    /**
     * Si la pregunta entró por voz, la respuesta se lee en voz alta.
     * Si se escribió con el teclado, solo se muestra en pantalla.
     */
    private var ultimaPreguntaFueVoz = false

    private val permisoMicLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) {
                empezarAEscuchar()
            } else {
                Toast.makeText(
                    this,
                    "Se necesita el micrófono para preguntar por voz",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val clase = intent.getStringExtra(EXTRA_CLASE_EQUIPO)
        val equipo = EquipoRepository.porClase(clase)
        equipoActual = equipo
        // Se asigna via supportActionBar (no binding.toolbar.title): al usar
        // setSupportActionBar(), AppCompat vuelve a aplicar su propio titulo
        // cacheado sobre el Toolbar despues de onCreate, y pisa cualquier
        // binding.toolbar.title asignado directamente.
        supportActionBar?.title = "Asistente: ${equipo?.nombre ?: "equipo no reconocido"}"

        adapter = ChatAdapter(mensajes)
        binding.rcChat.layoutManager = LinearLayoutManager(this)
        binding.rcChat.adapter = adapter

        if (equipo == null) {
            adapter.agregar(
                ChatMessage(
                    "No se reconoció el equipo. Vuelve a la pantalla de detección e inténtalo de nuevo.",
                    esUsuario = false
                )
            )
            binding.edtPregunta.isEnabled = false
            binding.btEnviar.isEnabled = false
            binding.btMic.isEnabled = false
            return
        }

        val tieneRag = equipo.vectorStoreId.isNotBlank() || OpenAIClient.VECTOR_STORE_GENERAL.isNotBlank()
        val aviso = if (tieneRag) "" else
            "\n\n⚠ Todavía no hay manuales del laboratorio cargados, así que responderé con la " +
                "ficha técnica interna de la app."

        adapter.agregar(
            ChatMessage(
                "Hola, soy el asistente del Laboratorio de Redes y Telecomunicaciones. " +
                    "Pregúntame sobre el ${equipo.nombre}: su función, componentes, " +
                    "procedimiento de uso, seguridad o mantenimiento. " +
                    "Puedes escribir o tocar el micrófono para hablar.$aviso",
                esUsuario = false
            )
        )

        binding.btEnviar.setOnClickListener {
            ultimaPreguntaFueVoz = false
            enviar(equipo, binding.edtPregunta.text?.toString()?.trim().orEmpty())
        }

        binding.btMic.setOnClickListener { alTocarMicrofono() }

        prepararTts()
    }

    // ---------------------------------------------------------------- voz: salida

    private fun prepararTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Si el dispositivo no tiene voz en español, queda en el idioma
                // por defecto: se prefiere eso a no hablar nada.
                val resultado = tts?.setLanguage(Locale("es", "ES"))
                if (resultado == TextToSpeech.LANG_MISSING_DATA ||
                    resultado == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    tts?.setLanguage(Locale.getDefault())
                }
                tts?.setSpeechRate(1.0f)
                ttsListo = true
            }
        }
    }

    /**
     * Lee la respuesta en voz alta. Se limpian los marcadores visuales (emoji de
     * fuentes, viñetas, asteriscos de markdown) porque el sintetizador los
     * pronunciaría literalmente.
     */
    private fun hablar(texto: String) {
        if (!ttsListo) return
        val limpio = texto
            .replace("📄", " Fuentes: ")
            .replace("⚠", " Atención: ")
            .replace(Regex("[*_#`]"), "")
            .replace(Regex("\\n+"), ". ")
            .trim()
        tts?.speak(limpio, TextToSpeech.QUEUE_FLUSH, null, "respuesta")
    }

    // ---------------------------------------------------------------- voz: entrada

    private fun alTocarMicrofono() {
        if (escuchando) {
            detenerEscucha()
            return
        }
        // Se corta cualquier respuesta que se esté leyendo: si no, el micrófono
        // capta la propia voz del teléfono.
        tts?.stop()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            empezarAEscuchar()
        } else {
            permisoMicLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun empezarAEscuchar() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                "Este teléfono no tiene reconocimiento de voz disponible. Escribe tu pregunta.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        reconocedor?.destroy()
        reconocedor = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) = mostrarEstado("Escuchando… habla ahora")
                override fun onBeginningOfSpeech() = mostrarEstado("Escuchando…")
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() = mostrarEstado("Reconociendo…")

                override fun onError(error: Int) {
                    escuchando = false
                    ocultarEstado()
                    val mensaje = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH,
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                            "No te escuché bien, intenta de nuevo"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                            "Falta el permiso de micrófono"
                        SpeechRecognizer.ERROR_NETWORK,
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            "Sin conexión para el reconocimiento de voz"
                        else -> "No se pudo reconocer la voz (código $error)"
                    }
                    Toast.makeText(this@ChatActivity, mensaje, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    escuchando = false
                    ocultarEstado()
                    val texto = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.trim()
                        .orEmpty()
                    if (texto.isEmpty()) {
                        Toast.makeText(this@ChatActivity, "No te escuché bien, intenta de nuevo", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val equipo = equipoActual ?: return
                    ultimaPreguntaFueVoz = true
                    enviar(equipo, texto)
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val parcial = partialResults
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                    if (!parcial.isNullOrBlank()) mostrarEstado("\"$parcial\"")
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        escuchando = true
        mostrarEstado("Escuchando… habla ahora")
        reconocedor?.startListening(intent)
    }

    private fun detenerEscucha() {
        escuchando = false
        reconocedor?.stopListening()
        ocultarEstado()
    }

    private fun mostrarEstado(texto: String) {
        runOnUiThread {
            binding.txtEstadoVoz.text = texto
            binding.txtEstadoVoz.visibility = View.VISIBLE
        }
    }

    private fun ocultarEstado() {
        runOnUiThread { binding.txtEstadoVoz.visibility = View.GONE }
    }

    // ---------------------------------------------------------------- RAG

    private fun enviar(equipo: Equipo, texto: String) {
        if (texto.isEmpty()) return

        adapter.agregar(ChatMessage(texto, esUsuario = true))
        binding.edtPregunta.setText("")
        binding.rcChat.scrollToPosition(adapter.itemCount - 1)
        binding.progress.visibility = View.VISIBLE
        binding.btEnviar.isEnabled = false
        binding.btMic.isEnabled = false

        val instrucciones = "Eres el asistente técnico del Laboratorio de Redes y Telecomunicaciones " +
            "de la UTEQ. Responde EXCLUSIVAMENTE con base en los documentos proporcionados " +
            "sobre el equipo ${equipo.nombre}. Cubre según corresponda: función, componentes " +
            "principales, procedimiento básico de uso, elementos de protección personal, riesgos " +
            "asociados y prácticas académicas relacionadas. Cita siempre la fuente consultada. " +
            "Si la información no está en los documentos, indica que no dispones de información " +
            "suficiente y recomienda consultar al docente o responsable del laboratorio. " +
            "Responde en español, de forma breve y concreta."

        OpenAIClient.preguntar(
            pregunta = texto,
            vectorStoreId = equipo.vectorStoreId,
            instrucciones = instrucciones,
            contextoRespaldo = fichaComoTexto(equipo),
            callback = object : OpenAIClient.RespuestaCallback {
                override fun onSuccess(respuesta: String) = mostrar(respuesta, leerEnVoz = ultimaPreguntaFueVoz)
                override fun onError(mensaje: String) = mostrar("⚠ $mensaje", leerEnVoz = false)
            }
        )
    }

    private fun mostrar(texto: String, leerEnVoz: Boolean) {
        runOnUiThread {
            binding.progress.visibility = View.GONE
            binding.btEnviar.isEnabled = true
            binding.btMic.isEnabled = true
            adapter.agregar(ChatMessage(texto, esUsuario = false))
            binding.rcChat.scrollToPosition(adapter.itemCount - 1)
            if (leerEnVoz) hablar(texto)
        }
    }

    /** Serializa la ficha técnica interna para usarla como contexto de respaldo. */
    private fun fichaComoTexto(e: Equipo): String = buildString {
        appendLine("Equipo: ${e.nombre} (clase del detector: ${e.clase})")
        appendLine("Categoría: ${e.categoria}")
        appendLine("Función: ${e.funcion}")
        appendLine("Componentes principales: ${e.componentes.joinToString("; ")}")
        appendLine("Procedimiento básico de uso: ${e.procedimientoBasico}")
        appendLine("Elementos de protección personal: ${e.epp.joinToString("; ")}")
        appendLine("Riesgos asociados: ${e.riesgos.joinToString("; ")}")
        appendLine("Prácticas académicas relacionadas: ${e.practicasRelacionadas}")
    }

    override fun onPause() {
        super.onPause()
        // Sin esto, la respuesta se sigue leyendo aunque salgas de la pantalla.
        tts?.stop()
        if (escuchando) detenerEscucha()
    }

    override fun onDestroy() {
        super.onDestroy()
        reconocedor?.destroy()
        reconocedor = null
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
