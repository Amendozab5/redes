package com.example.proyecto.detection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.data.EquipoRepository
import com.example.proyecto.databinding.ItemChipEquipoBinding

/**
 * Chips horizontales con los equipos detectados en el frame actual: nombre
 * comercial del equipo y porcentaje de confianza. El chip seleccionado se
 * resalta y es el que alimenta la ficha técnica y el asistente.
 */
class DetectedChipAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<DetectedChipAdapter.ChipViewHolder>() {

    /** Una entrada por clase, con la mejor confianza vista en el frame. */
    data class Item(val clase: String, val confianza: Float)

    private val items = mutableListOf<Item>()
    private var claseSeleccionada: String? = null

    fun actualizar(nuevos: List<Item>, seleccion: String?) {
        val cambio = nuevos != items || seleccion != claseSeleccionada
        if (!cambio) return
        items.clear()
        items.addAll(nuevos)
        claseSeleccionada = seleccion
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemChipEquipoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.binding.root.context
        val seleccionado = item.clase == claseSeleccionada

        // Nombre comercial si la clase está en el repositorio; si no, la clase cruda.
        holder.binding.txtNombre.text = EquipoRepository.porClase(item.clase)?.nombre ?: item.clase
        holder.binding.txtConfianza.text = "${(item.confianza * 100).toInt()}%"

        holder.binding.root.setBackgroundResource(
            if (seleccionado) R.drawable.bg_chip_seleccionado else R.drawable.bg_chip_normal
        )
        holder.binding.imgCheck.visibility = if (seleccionado) View.VISIBLE else View.GONE

        val colorTexto = ContextCompat.getColor(
            ctx, if (seleccionado) R.color.white else R.color.texto_principal
        )
        holder.binding.txtNombre.setTextColor(colorTexto)
        holder.binding.txtConfianza.setTextColor(colorTexto)
        holder.binding.txtConfianza.alpha = if (seleccionado) 0.85f else 0.7f

        holder.binding.root.setOnClickListener { onClick(item.clase) }
    }

    override fun getItemCount() = items.size

    class ChipViewHolder(val binding: ItemChipEquipoBinding) : RecyclerView.ViewHolder(binding.root)
}
