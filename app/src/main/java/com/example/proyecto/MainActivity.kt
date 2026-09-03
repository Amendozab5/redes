package com.example.proyecto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.databinding.ActivityMainBinding
import com.example.proyecto.detection.DetectedChipAdapter
import com.example.proyecto.detection.YoloDetector
import com.example.proyecto.ui.FichaTecnicaActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Pantalla principal: cámara en vivo + detección en tiempo real de equipos del
 * Laboratorio de Redes y Telecomunicaciones. Al tocar un chip de equipo detectado
 * se abre su ficha técnica (y desde ahí, el chat con el asistente RAG).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var detector: YoloDetector
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var chipAdapter: DetectedChipAdapter

    private val permisoCamaraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) {
                iniciarCamara()
            } else {
                Toast.makeText(this, "Se requiere permiso de cámara para detectar equipos", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detector = YoloDetector(this)
        if (!detector.isReady) {
            Toast.makeText(
                this,
                "Modelo no encontrado: copia redes_uteq.tflite en app/src/main/assets/ (ver LEEME.txt)",
                Toast.LENGTH_LONG
            ).show()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        chipAdapter = DetectedChipAdapter { clase ->
            startActivity(
                Intent(this, FichaTecnicaActivity::class.java)
                    .putExtra(FichaTecnicaActivity.EXTRA_CLASE_EQUIPO, clase)
            )
        }
        binding.rcDetectados.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcDetectados.adapter = chipAdapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara()
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun iniciarCamara() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy -> analizarFrame(imageProxy) }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            } catch (e: Exception) {
                Toast.makeText(this, "Error iniciando la cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analizarFrame(imageProxy: ImageProxy) {
        if (!detector.isReady) {
            imageProxy.close()
            return
        }
        try {
            val bitmap = rotarBitmap(imageProxyToBitmap(imageProxy), imageProxy.imageInfo.rotationDegrees)
            val detecciones = detector.detect(bitmap)
            runOnUiThread {
                binding.overlay.setDetections(detecciones, bitmap.width, bitmap.height)
                chipAdapter.actualizar(detecciones.map { it.label })
            }
        } catch (e: Exception) {
            // Se descarta un frame fallido para no interrumpir la vista en vivo.
        } finally {
            imageProxy.close()
        }
    }

    /**
     * Convierte un frame RGBA_8888 (ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888) a Bitmap.
     * El byte order RGBA de este formato coincide con el de Bitmap.Config.ARGB_8888 en Android.
     */
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val rowPadding = rowStride - pixelStride * imageProxy.width

        val bitmapConRelleno = Bitmap.createBitmap(
            imageProxy.width + rowPadding / pixelStride,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )
        bitmapConRelleno.copyPixelsFromBuffer(buffer)

        return if (rowPadding == 0) {
            bitmapConRelleno
        } else {
            Bitmap.createBitmap(bitmapConRelleno, 0, 0, imageProxy.width, imageProxy.height)
        }
    }

    private fun rotarBitmap(bitmap: Bitmap, grados: Int): Bitmap {
        if (grados == 0) return bitmap
        val matrix = Matrix().apply { postRotate(grados.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        detector.close()
    }
}
