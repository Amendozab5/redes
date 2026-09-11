package com.example.proyecto.detection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.proyecto.data.EquipoRepository
import kotlin.math.abs

/**
 * Dibuja los cuadros delimitadores sobre la vista previa de la cámara: un color
 * distinto por clase, etiqueta redondeada con el nombre del equipo y su
 * porcentaje de confianza. El cuadro de la clase seleccionada se dibuja más
 * grueso para que se distinga del resto.
 *
 * Asume que la PreviewView usa ajuste "fit center" (la imagen completa cabe en
 * la vista, con barras si el aspect ratio no coincide).
 */
class DetectionOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var detections: List<Detection> = emptyList()
    private var imageWidth = 0
    private var imageHeight = 0
    private var claseSeleccionada: String? = null

    private val paleta = intArrayOf(
        Color.parseColor("#EF4444"), Color.parseColor("#F59E0B"),
        Color.parseColor("#10B981"), Color.parseColor("#3B82F6"),
        Color.parseColor("#A855F7"), Color.parseColor("#06B6D4"),
        Color.parseColor("#EC4899"), Color.parseColor("#84CC16"),
        Color.parseColor("#F97316")
    )

    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val fondoEtiqueta = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textoEtiqueta = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 34f
        isFakeBoldText = true
    }

    private val rectAux = RectF()

    fun setDetections(
        detecciones: List<Detection>,
        anchoImagen: Int,
        altoImagen: Int,
        seleccionada: String? = null
    ) {
        detections = detecciones
        imageWidth = anchoImagen
        imageHeight = altoImagen
        claseSeleccionada = seleccionada
        invalidate()
    }

    private fun colorDe(clase: String): Int = paleta[abs(clase.hashCode()) % paleta.size]

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (imageWidth <= 0 || imageHeight <= 0) return

        val scale = minOf(width.toFloat() / imageWidth, height.toFloat() / imageHeight)
        val offsetX = (width - imageWidth * scale) / 2f
        val offsetY = (height - imageHeight * scale) / 2f

        for (d in detections) {
            val color = colorDe(d.label)
            val seleccionado = d.label == claseSeleccionada

            val left = offsetX + d.box.left * imageWidth * scale
            val top = offsetY + d.box.top * imageHeight * scale
            val right = offsetX + d.box.right * imageWidth * scale
            val bottom = offsetY + d.box.bottom * imageHeight * scale

            boxPaint.color = color
            boxPaint.strokeWidth = if (seleccionado) 9f else 5f
            rectAux.set(left, top, right, bottom)
            canvas.drawRoundRect(rectAux, 12f, 12f, boxPaint)

            val nombre = EquipoRepository.porClase(d.label)?.nombre ?: d.label
            val etiqueta = "$nombre  ${(d.confidence * 100).toInt()}%"
            val anchoTexto = textoEtiqueta.measureText(etiqueta)
            val altoCaja = 46f
            val etiquetaTop = (top - altoCaja - 6f).coerceAtLeast(0f)

            fondoEtiqueta.color = color
            rectAux.set(left, etiquetaTop, left + anchoTexto + 24f, etiquetaTop + altoCaja)
            canvas.drawRoundRect(rectAux, 10f, 10f, fondoEtiqueta)
            canvas.drawText(etiqueta, left + 12f, etiquetaTop + 32f, textoEtiqueta)
        }
    }
}
