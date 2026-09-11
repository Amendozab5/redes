package com.example.proyecto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.data.EquipoRepository
import com.example.proyecto.databinding.ActivityMainBinding
import com.example.proyecto.detection.DetectedChipAdapter
import com.example.proyecto.detection.YoloDetector
import com.example.proyecto.ui.ChatActivity
import com.example.proyecto.ui.FichaTecnicaActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Pantalla principal: cámara en vivo + detección en tiempo real de equipos del
 * Laboratorio de Redes y Telecomunicaciones.
 *
 * El panel inferior muestra los equipos detectados como chips (nombre + % de
 * confianza). Al tocar un chip se selecciona ese equipo, y desde ahí se abre su
 * ficha técnica o el asistente inteligente (LLM + RAG).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var detector: YoloDetector
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var chipAdapter: DetectedChipAdapter

    /** Clase del equipo que el usuario tiene seleccionado (o la última detectada). */
    private var claseSeleccionada: String? = null

    /**
     * La detección corre a ~10 fps, pero refrescar los chips a esa velocidad los
     * hace parpadear y hace imposible tocarlos. Los cuadros del overlay sí se
     * actualizan en cada frame; la lista de chips solo cada INTERVALO_UI_MS.
     */
    private var ultimaActualizacionChips = 0L

    private companion object {
        const val INTERVALO_UI_MS = 400L
    }

    private val permisoCamaraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) {
                iniciarCamara()
            } else {
                Toast.makeText(
                    this,
                    "Se requiere permiso de cámara para detectar equipos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aplicarInsets()

        detector = YoloDetector(this)
        if (!detector.isReady) {
            Toast.makeText(
                this,
                "Modelo no encontrado: copia redes_uteq.tflite en app/src/main/assets/ (ver LEEME.txt)",
                Toast.LENGTH_LONG
            ).show()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        chipAdapter = DetectedChipAdapter { clase -> seleccionar(clase) }
        binding.rcDetectados.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcDetectados.adapter = chipAdapter

        binding.btFicha.setOnClickListener {
            val clase = claseSeleccionada ?: return@setOnClickListener
            startActivity(
                Intent(this, FichaTecnicaActivity::class.java)
                    .putExtra(FichaTecnicaActivity.EXTRA_CLASE_EQUIPO, clase)
            )
        }

        binding.btAsistente.setOnClickListener {
            val clase = claseSeleccionada ?: return@setOnClickListener
            startActivity(
                Intent(this, ChatActivity::class.java)
                    .putExtra(ChatActivity.EXTRA_CLASE_EQUIPO, clase)
            )
        }

        actualizarAcciones()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarCamara()
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * La app dibuja edge-to-edge (targetSdk 35+): sin esto, la cabecera queda
     * tapada por la barra de estado y el panel inferior por la de navegación.
     */
    private fun aplicarInsets() {
        val margenHeaderBase = dpToPx(12)
        val paddingPanelBase = dpToPx(10)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            (binding.header.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.let { lp ->
                lp.topMargin = systemBars.top + margenHeaderBase
                binding.header.layoutParams = lp
            }
            binding.panelInferior.setPadding(
                binding.panelInferior.paddingLeft,
                binding.panelInferior.paddingTop,
                binding.panelInferior.paddingRight,
                systemBars.bottom + paddingPanelBase
            )
            insets
        }
    }

    private fun seleccionar(clase: String) {
        claseSeleccionada = clase
        actualizarAcciones()
    }

    /** Habilita/deshabilita los botones y actualiza el texto de la selección. */
    private fun actualizarAcciones() {
        val clase = claseSeleccionada
        val habilitado = clase != null
        binding.btFicha.isEnabled = habilitado
        binding.btFicha.alpha = if (habilitado) 1f else 0.45f
        binding.btAsistente.isEnabled = habilitado
        binding.btAsistente.alpha = if (habilitado) 1f else 0.45f

        binding.txtSeleccion.text = if (clase == null) {
            getString(R.string.sin_detecciones)
        } else {
            EquipoRepository.porClase(clase)?.nombre ?: clase
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
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
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

            // Una entrada por clase, con la mejor confianza del frame, de mayor a menor.
            val porClase = detecciones
                .groupBy { it.label }
                .map { (clase, lista) ->
                    DetectedChipAdapter.Item(clase, lista.maxOf { it.confidence })
                }
                .sortedByDescending { it.confianza }

            val ahora = System.currentTimeMillis()
            val tocaRefrescarChips = ahora - ultimaActualizacionChips >= INTERVALO_UI_MS

            runOnUiThread {
                binding.overlay.setDetections(
                    detecciones, bitmap.width, bitmap.height, claseSeleccionada
                )

                if (tocaRefrescarChips) {
                    ultimaActualizacionChips = ahora

                    // Si no hay nada seleccionado todavía, se preselecciona la
                    // detección más confiable para que los botones sirvan de una.
                    if (claseSeleccionada == null && porClase.isNotEmpty()) {
                        claseSeleccionada = porClase.first().clase
                        actualizarAcciones()
                    }

                    binding.txtContador.text = porClase.size.toString()
                    binding.txtContador.visibility =
                        if (porClase.isEmpty()) View.INVISIBLE else View.VISIBLE
                    chipAdapter.actualizar(porClase, claseSeleccionada)
                }
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

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

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
