package com.example.proyecto.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.data.EquipoRepository
import com.example.proyecto.databinding.ActivityChatBinding
import com.example.proyecto.network.OpenAIClient

/**
 * Chat con el asistente inteligente (RAG) sobre un equipo específico. La respuesta
 * debe basarse únicamente en los manuales/guías subidos al Vector Store del equipo
 * (ver EquipoRepository.vectorStoreId).
 */
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLASE_EQUIPO = "extra_clase_equipo"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private val mensajes = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val clase = intent.getStringExtra(EXTRA_CLASE_EQUIPO)
        val equipo = EquipoRepository.porClase(clase)
        // Se asigna via supportActionBar (no binding.toolbar.title): al usar
        // setSupportActionBar(), AppCompat vuelve a aplicar su propio titulo
        // cacheado sobre el Toolbar despues de onCreate, y pisa cualquier
        // binding.toolbar.title asignado directamente.
        supportActionBar?.title = "Asistente: ${equipo?.nombre ?: "equipo no reconocido"}"

        adapter = ChatAdapter(mensajes)
        binding.rcChat.layoutManager = LinearLayoutManager(this)
        binding.rcChat.adapter = adapter

        if (equipo != null) {
            adapter.agregar(
                ChatMessage(
                    "Hola, soy el asistente del Laboratorio de Redes y Telecomunicaciones. " +
                        "Pregúntame sobre el ${equipo.nombre}: su función, componentes, " +
                        "procedimiento de uso, seguridad o mantenimiento.",
                    esUsuario = false
                )
            )
        } else {
            adapter.agregar(ChatMessage("No se reconoció el equipo. Vuelve a la ficha técnica e inténtalo de nuevo.", esUsuario = false))
            binding.edtPregunta.isEnabled = false
            binding.btEnviar.isEnabled = false
        }

        binding.btEnviar.setOnClickListener {
            if (equipo == null) return@setOnClickListener
            val texto = binding.edtPregunta.text?.toString()?.trim().orEmpty()
            if (texto.isEmpty()) return@setOnClickListener

            adapter.agregar(ChatMessage(texto, esUsuario = true))
            binding.edtPregunta.setText("")
            binding.rcChat.scrollToPosition(adapter.itemCount - 1)
            binding.progress.visibility = View.VISIBLE
            binding.btEnviar.isEnabled = false

            val instrucciones = "Eres el asistente técnico del Laboratorio de Redes y Telecomunicaciones " +
                "de la UTEQ. Responde EXCLUSIVAMENTE con base en los manuales y guías proporcionados " +
                "sobre el equipo ${equipo.nombre}. Cubre según corresponda: función, componentes " +
                "principales, procedimiento básico de uso, elementos de protección personal, riesgos " +
                "asociados y prácticas académicas relacionadas. Cita la fuente consultada. Si la " +
                "información no está en los documentos, indica que no dispones de información " +
                "suficiente y recomienda consultar al docente o responsable del laboratorio."

            OpenAIClient.preguntar(
                pregunta = texto,
                vectorStoreId = equipo.vectorStoreId,
                instrucciones = instrucciones,
                callback = object : OpenAIClient.RespuestaCallback {
                    override fun onSuccess(respuesta: String) {
                        runOnUiThread {
                            binding.progress.visibility = View.GONE
                            binding.btEnviar.isEnabled = true
                            adapter.agregar(ChatMessage(respuesta, esUsuario = false))
                            binding.rcChat.scrollToPosition(adapter.itemCount - 1)
                        }
                    }

                    override fun onError(mensaje: String) {
                        runOnUiThread {
                            binding.progress.visibility = View.GONE
                            binding.btEnviar.isEnabled = true
                            adapter.agregar(ChatMessage("⚠ $mensaje", esUsuario = false))
                            binding.rcChat.scrollToPosition(adapter.itemCount - 1)
                        }
                    }
                }
            )
        }
    }
}
