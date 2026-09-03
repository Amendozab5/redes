package com.example.proyecto.detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Envoltorio sobre TensorFlow Lite Interpreter para correr el modelo YOLO11n entrenado
 * por el compañero de grupo (dataset "equipos-redes-uteq-2026", exportado desde
 * Ultralytics a .tflite): entrada [1,3,640,640] (NCHW, detectado automáticamente),
 * salida [1,10,8400] = 4 coords (cx,cy,w,h normalizadas 0..1) + 6 clases, sin NMS
 * integrado (se aplica NMS manual en [detect]). Verificado directamente contra
 * modelo_redes_uteq.tflite (metadata.json embebida y una inferencia real).
 *
 * Si no encuentra "modelPath" o "labelsPath" en assets/, [isReady] queda en false
 * y [detect] siempre devuelve una lista vacía en vez de fallar la app.
 */
class YoloDetector(
    context: Context,
    private val modelPath: String = "redes_uteq.tflite",
    private val labelsPath: String = "labels.txt",
    private val confidenceThreshold: Float = 0.5f,
    private val iouThreshold: Float = 0.45f
) {
    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()
    private var inputWidth = 640
    private var inputHeight = 640

    // El export de Ultralytics puede entregar el tensor de entrada en NCHW
    // [1, 3, H, W] o en NHWC [1, H, W, 3]; se detecta automáticamente en el init.
    private var channelsFirst = false

    val isReady: Boolean get() = interpreter != null

    init {
        try {
            val model = loadModelFile(context, modelPath)
            interpreter = Interpreter(model, Interpreter.Options().apply { setNumThreads(4) })
            labels = loadLabels(context, labelsPath)
            interpreter?.getInputTensor(0)?.shape()?.let { shape ->
                if (shape.size == 4) {
                    if (shape[1] == 3) {
                        // NCHW: [1, 3, H, W]
                        channelsFirst = true
                        inputHeight = shape[2]
                        inputWidth = shape[3]
                    } else {
                        // NHWC: [1, H, W, 3]
                        channelsFirst = false
                        inputHeight = shape[1]
                        inputWidth = shape[2]
                    }
                }
            }
        } catch (e: Exception) {
            // Modelo o labels no encontrados todavía en assets/: la app sigue
            // funcionando (cámara en vivo) pero sin detecciones hasta integrarlo.
            interpreter = null
        }
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val afd = context.assets.openFd(modelPath)
        FileInputStream(afd.fileDescriptor).use { input ->
            val channel = input.channel
            return channel.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.declaredLength)
        }
    }

    private fun loadLabels(context: Context, labelsPath: String): List<String> =
        context.assets.open(labelsPath).bufferedReader().useLines { lines ->
            lines.map { it.trim() }.filter { it.isNotEmpty() }.toList()
        }

    fun detect(bitmap: Bitmap): List<Detection> {
        val interp = interpreter ?: return emptyList()

        val resized = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
        val inputBuffer = bitmapToByteBuffer(resized)

        val outputShape = interp.getOutputTensor(0).shape() // [1, 4+numClases, numCajas]
        if (outputShape.size != 3) return emptyList()

        val numAttrs = outputShape[1]
        val numBoxes = outputShape[2]
        val numClasses = numAttrs - 4
        if (numClasses <= 0) return emptyList()

        val output = Array(1) { Array(numAttrs) { FloatArray(numBoxes) } }
        interp.run(inputBuffer, output)
        val raw = output[0]

        val candidatos = mutableListOf<Detection>()
        for (i in 0 until numBoxes) {
            var mejorClase = -1
            var mejorScore = 0f
            for (c in 0 until numClasses) {
                val score = raw[4 + c][i]
                if (score > mejorScore) {
                    mejorScore = score
                    mejorClase = c
                }
            }
            if (mejorScore < confidenceThreshold || mejorClase < 0) continue

            var cx = raw[0][i]
            var cy = raw[1][i]
            var w = raw[2][i]
            var h = raw[3][i]
            // Algunos exports de Ultralytics devuelven las cajas en píxeles del
            // tamaño de entrada (0..inputWidth/Height) en vez de normalizadas 0..1.
            if (cx > 1.5f || cy > 1.5f || w > 1.5f || h > 1.5f) {
                cx /= inputWidth
                cy /= inputHeight
                w /= inputWidth
                h /= inputHeight
            }

            val left = (cx - w / 2f).coerceIn(0f, 1f)
            val top = (cy - h / 2f).coerceIn(0f, 1f)
            val right = (cx + w / 2f).coerceIn(0f, 1f)
            val bottom = (cy + h / 2f).coerceIn(0f, 1f)
            val etiqueta = labels.getOrElse(mejorClase) { "clase_$mejorClase" }
            candidatos.add(Detection(etiqueta, mejorScore, RectF(left, top, right, bottom)))
        }

        return nonMaxSuppression(candidatos)
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * inputWidth * inputHeight * 3)
        buffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputWidth * inputHeight)
        bitmap.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        if (channelsFirst) {
            // NCHW: primero todos los valores de R, luego todos los de G, luego todos los de B
            for (pixel in pixels) buffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
            for (pixel in pixels) buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
            for (pixel in pixels) buffer.putFloat((pixel and 0xFF) / 255f)
        } else {
            // NHWC: R, G, B intercalados por píxel
            for (pixel in pixels) {
                buffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
                buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
                buffer.putFloat((pixel and 0xFF) / 255f)
            }
        }
        buffer.rewind()
        return buffer
    }

    private fun nonMaxSuppression(detecciones: List<Detection>): List<Detection> {
        val resultado = mutableListOf<Detection>()
        val porClase = detecciones.groupBy { it.label }
        for ((_, grupo) in porClase) {
            val pendientes = grupo.sortedByDescending { it.confidence }.toMutableList()
            while (pendientes.isNotEmpty()) {
                val mejor = pendientes.removeAt(0)
                resultado.add(mejor)
                pendientes.removeAll { iou(mejor.box, it.box) > iouThreshold }
            }
        }
        return resultado
    }

    private fun iou(a: RectF, b: RectF): Float {
        val interLeft = maxOf(a.left, b.left)
        val interTop = maxOf(a.top, b.top)
        val interRight = minOf(a.right, b.right)
        val interBottom = minOf(a.bottom, b.bottom)
        val interArea = maxOf(0f, interRight - interLeft) * maxOf(0f, interBottom - interTop)
        val areaA = (a.right - a.left) * (a.bottom - a.top)
        val areaB = (b.right - b.left) * (b.bottom - b.top)
        val union = areaA + areaB - interArea
        return if (union <= 0f) 0f else interArea / union
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
