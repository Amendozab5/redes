package com.example.proyecto.detection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Dibuja los cuadros delimitadores y etiquetas sobre la vista previa de la cámara.
 * Asume que la [PreviewView] usa un ajuste tipo "fit center" (la imagen completa
 * cabe dentro de la vista, con barras si no coincide el aspect ratio).
 */
class DetectionOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var detections: List<Detection> = emptyList()
    private var imageWidth = 0
    private var imageHeight = 0

    private val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#00E676")
        isAntiAlias = true
    }

    private val labelBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#CC00E676")
    }

    private val labelTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 38f
        isFakeBoldText = true
        isAntiAlias = true
    }

    fun setDetections(detecciones: List<Detection>, anchoImagen: Int, altoImagen: Int) {
        detections = detecciones
        imageWidth = anchoImagen
        imageHeight = altoImagen
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (imageWidth <= 0 || imageHeight <= 0) return

        val scale = minOf(width.toFloat() / imageWidth, height.toFloat() / imageHeight)
        val offsetX = (width - imageWidth * scale) / 2f
        val offsetY = (height - imageHeight * scale) / 2f

        for (d in detections) {
            val left = offsetX + d.box.left * imageWidth * scale
            val top = offsetY + d.box.top * imageHeight * scale
            val right = offsetX + d.box.right * imageWidth * scale
            val bottom = offsetY + d.box.bottom * imageHeight * scale

            canvas.drawRect(left, top, right, bottom, boxPaint)

            val etiqueta = "${d.label} ${(d.confidence * 100).toInt()}%"
            val anchoTexto = labelTextPaint.measureText(etiqueta)
            val fondoTop = (top - 46f).coerceAtLeast(0f)
            canvas.drawRect(left, fondoTop, left + anchoTexto + 16f, fondoTop + 46f, labelBackgroundPaint)
            canvas.drawText(etiqueta, left + 8f, fondoTop + 34f, labelTextPaint)
        }
    }
}
