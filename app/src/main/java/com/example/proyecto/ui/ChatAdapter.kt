package com.example.proyecto.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.databinding.ItemChatBotBinding
import com.example.proyecto.databinding.ItemChatUserBinding

class ChatAdapter(
    private val mensajes: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TIPO_USUARIO = 0
        private const val TIPO_BOT = 1
    }

    override fun getItemViewType(position: Int): Int =
        if (mensajes[position].esUsuario) TIPO_USUARIO else TIPO_BOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPO_USUARIO) {
            UsuarioViewHolder(ItemChatUserBinding.inflate(inflater, parent, false))
        } else {
            BotViewHolder(ItemChatBotBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = mensajes[position]
        when (holder) {
            is UsuarioViewHolder -> holder.binding.txtMensaje.text = mensaje.texto
            is BotViewHolder -> holder.binding.txtMensaje.text = mensaje.texto
        }
    }

    override fun getItemCount(): Int = mensajes.size

    fun agregar(mensaje: ChatMessage) {
        mensajes.add(mensaje)
        notifyItemInserted(mensajes.size - 1)
    }

    class UsuarioViewHolder(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
    class BotViewHolder(val binding: ItemChatBotBinding) : RecyclerView.ViewHolder(binding.root)
}
