package com.example.proyecto.detection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.databinding.ItemChipEquipoBinding

/**
 * Muestra, como chips horizontales, las clases de equipo detectadas en el frame
 * actual (sin duplicados). Al tocar un chip se abre la ficha técnica del equipo.
 */
class DetectedChipAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<DetectedChipAdapter.ChipViewHolder>() {

    private val clases = mutableListOf<String>()

    fun actualizar(nuevasClases: List<String>) {
        val distintas = nuevasClases.distinct()
        if (distintas == clases) return
        clases.clear()
        clases.addAll(distintas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val binding = ItemChipEquipoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val clase = clases[position]
        holder.binding.root.text = clase
        holder.binding.root.setOnClickListener { onClick(clase) }
    }

    override fun getItemCount() = clases.size

    class ChipViewHolder(val binding: ItemChipEquipoBinding) : RecyclerView.ViewHolder(binding.root)
}
