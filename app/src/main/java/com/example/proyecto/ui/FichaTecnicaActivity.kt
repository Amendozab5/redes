package com.example.proyecto.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.data.EquipoRepository
import com.example.proyecto.databinding.ActivityFichaTecnicaBinding

/** Ficha técnica del equipo seleccionado desde la pantalla de detección. */
class FichaTecnicaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLASE_EQUIPO = "extra_clase_equipo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFichaTecnicaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val clase = intent.getStringExtra(EXTRA_CLASE_EQUIPO)
        val equipo = EquipoRepository.porClase(clase)

        if (equipo == null) {
            binding.toolbar.title = "Equipo no reconocido"
            binding.txtCategoria.text = ""
            binding.txtFuncion.text =
                "No se encontró información para la clase \"$clase\" en EquipoRepository."
            binding.btChat.isEnabled = false
            return
        }

        binding.toolbar.title = equipo.nombre
        binding.txtCategoria.text = equipo.categoria
        binding.txtFuncion.text = equipo.funcion
        binding.txtComponentes.text = equipo.componentes.joinToString("\n") { "• $it" }
        binding.txtProcedimiento.text = equipo.procedimientoBasico
        binding.txtEpp.text = equipo.epp.joinToString("\n") { "• $it" }
        binding.txtRiesgos.text = equipo.riesgos.joinToString("\n") { "• $it" }
        binding.txtPracticas.text = equipo.practicasRelacionadas

        binding.btChat.setOnClickListener {
            startActivity(
                Intent(this, ChatActivity::class.java)
                    .putExtra(ChatActivity.EXTRA_CLASE_EQUIPO, equipo.clase)
            )
        }
    }
}
