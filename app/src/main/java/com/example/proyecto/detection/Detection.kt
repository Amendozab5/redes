package com.example.proyecto.detection

import android.graphics.RectF

/**
 * Resultado de una detección. [box] usa coordenadas normalizadas (0..1) relativas
 * a la imagen analizada, para poder escalarlas fácilmente a cualquier vista.
 */
data class Detection(
    val label: String,
    val confidence: Float,
    val box: RectF
)
