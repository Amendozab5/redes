package com.example.proyecto.ui

import android.text.Spanned
import androidx.core.text.HtmlCompat

/**
 * Formateador mínimo de Markdown a texto con estilos.
 *
 * El modelo responde en Markdown (**negrita**, ### títulos, bloques ```), pero un
 * TextView normal muestra esos símbolos tal cual y la respuesta se ve sucia.
 * Aquí se convierten las marcas más comunes a HTML y se renderizan con
 * [HtmlCompat], que el TextView sí entiende.
 *
 * No es un parser completo de Markdown a propósito: solo cubre lo que el
 * asistente realmente usa. Añadir una librería entera para esto sería
 * desproporcionado.
 */
object MarkdownLigero {

    fun aTextoConEstilo(markdown: String): Spanned {
        var t = markdown.trim()

        // 1. Se escapan los caracteres de HTML ANTES de meter etiquetas propias,
        //    para que un "<" del texto original no rompa el renderizado.
        t = t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

        // 2. Bloques de código ```...```: se quitan las comillas y se deja el
        //    contenido en monoespaciado.
        t = Regex("```[a-zA-Z]*\\n?([\\s\\S]*?)```").replace(t) { m ->
            "<tt>" + m.groupValues[1].trim() + "</tt>"
        }
        // Código en línea `algo`
        t = Regex("`([^`\\n]+)`").replace(t) { m -> "<tt>${m.groupValues[1]}</tt>" }

        // 3. Títulos (### Algo) -> negrita, no encabezados gigantes.
        t = Regex("(?m)^#{1,6}\\s*(.+)$").replace(t) { m -> "<b>${m.groupValues[1]}</b>" }

        // 4. Negrita y cursiva. La negrita va primero: si no, los "*" sueltos de
        //    "**texto**" se consumirían como cursiva y quedaría a medias.
        t = Regex("\\*\\*([^*]+)\\*\\*").replace(t) { m -> "<b>${m.groupValues[1]}</b>" }
        t = Regex("(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)").replace(t) { m -> "<i>${m.groupValues[1]}</i>" }

        // 5. Viñetas "- " o "* " al inicio de línea -> bullet real.
        t = Regex("(?m)^\\s*[-*]\\s+").replace(t, "  •  ")

        // 6. La línea de fuentes se separa y se pone en cursiva para que se note
        //    que es la cita del documento consultado.
        t = t.replace(Regex("(?m)^📄\\s*(.+)$"), "<br><i>📄 $1</i>")

        // 7. Saltos de línea -> <br>, que es lo que entiende HtmlCompat.
        t = t.replace("\n", "<br>")

        return HtmlCompat.fromHtml(t, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
}
