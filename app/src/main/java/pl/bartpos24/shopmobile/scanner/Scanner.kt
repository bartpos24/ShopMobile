package pl.bartpos24.shopmobile.scanner

import android.util.Log
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import okhttp3.Dispatcher
import pl.bartpos24.shopmobile.ShopMobileApplication
import pl.bartpos24.shopmobile.models.BarcodeResult
import pl.bartpos24.shopmobile.utilities.BarcodeAnalyzer
import java.util.concurrent.ExecutorService

class Scanner(private val applicationContext: ShopMobileApplication) {
    companion object {
        private const val TAG = "Scanner"

        //fun newInstance() = BarcodeScannerFragment()
    }
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private val _scannerDataFlow = MutableStateFlow<String>("")
    val scannerDataFlow: StateFlow<String> = _scannerDataFlow

    fun scannerFlow(): Flow<String> {
        return scannerDataFlow.filter { it.isNotEmpty() }
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed")

        // Preview use case
        preview = Preview.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        // Image analysis use case
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                    _scannerDataFlow.value = barcode.rawValue ?: ""
                })
            }
    }
}