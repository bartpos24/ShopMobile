package pl.bartpos24.shopmobile.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.bartpos24.shopmobile.dagger.Injectable
import pl.bartpos24.shopmobile.databinding.BarcodeScannerFragmentBinding
import pl.bartpos24.shopmobile.databinding.LoginFragmentBinding
import pl.bartpos24.shopmobile.models.BarcodeResult
import pl.bartpos24.shopmobile.utilities.BarcodeAnalyzer
import pl.bartpos24.shopmobile.utilities.autoClearedView
import pl.bartpos24.shopmobile.utilities.navGraphShopMobileViewModels
import pl.bartpos24.shopmobile.viewmodels.BarcodeScannerViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerFragment(_context: Context, _binding: BarcodeScannerFragmentBinding, navId: Int) : ShopMobileFragment(), Injectable {
    private var binding = _binding
    private val viewModel: BarcodeScannerViewModel by navGraphShopMobileViewModels(navId)

    private var parentContext = _context
    //private lateinit var scalling: DynamicInterfaceScaling

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService

    private var previewView: PreviewView? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BarcodeScannerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        checkCameraPermission()

//        viewModel.barcodeResults
//            .mapNotNull { it }
//            .onEach {
//                binding.previewView.visibility = View.GONE
//            }
//            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed")

        val rotation = binding.previewView.display?.rotation ?: 0//previewView?.display?.rotation ?: 0

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
                    lifecycleScope.launch {
                        viewModel.emitBarcodeResult(
                            BarcodeResult(
                                rawValue = barcode.rawValue ?: "",
                                format = barcode.format
                            )
                        )
                    }
                })
            }

        // Select back camera
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            preview?.setSurfaceProvider(binding.previewView?.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }

    fun getBarcodeResult(): BarcodeResult? {
        return viewModel.barcodeResults.asLiveData().value
    }

    fun clearData() {
        viewModel.clearData()
    }

    companion object {
        private const val TAG = "BarcodeScannerFragment"

        //fun newInstance() = BarcodeScannerFragment()
    }
}