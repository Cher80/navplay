package com.example.nav.features.camerasber

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nav.base.BaseFragment
import com.example.nav.databinding.FragmentCameraBinding
import com.example.nav.databinding.FragmentCameraSberBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import ru.sberdevices.salutevision.SaluteVisionSdk
import ru.sberdevices.salutevision.core.data.SaluteVisionImage
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraSberFragment : BaseFragment() {

    private var binding: FragmentCameraSberBinding? = null

    private val analysisUseCase = ImageAnalysis.Builder()
        .build().apply {
            setAnalyzer(Executors.newSingleThreadExecutor(), YourImageAnalyzer())
        }

    private val viewModel by viewModels<CameraSberViewModel>()

    private val recognizer = SaluteVisionSdk.createBarcodeRecognizer()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
        }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("gnavlife", "onAttach Camera")
        viewModel.hashCode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.i = savedInstanceState?.getInt("i") ?: 0
        Log.d("gnavlife", "onCreate Camera savedInstanceState=$savedInstanceState")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("gnavlife", "onCreateView Camera")
        viewModel.onCreateView()
        binding = FragmentCameraSberBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("gnavlife", "onViewCreated Camera")


//        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }


    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, analysisUseCase
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("gnavlife", "onDestroyView Camera")
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("gnavlife", "onDestroy Camera")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("gnavlife", "onDetach Camera")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("i", viewModel.i)
        Log.d("gnavlife", "onSaveInstanceState Camera outState=$outState")
        super.onSaveInstanceState(outState)
    }


    inner class YourImageAnalyzer : ImageAnalysis.Analyzer {

        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {

                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                Log.d("dasdasd", "image = $image")


                val visionImage = SaluteVisionImage(
                    requireContext(),
                    imageProxy.image!!,
                    imageProxy.imageInfo.rotationDegrees
                )

                val codes = recognizer.process(visionImage, null)


                // Обработка результатов
                if (codes.isNotEmpty()) {
                    Log.d("dasdasdff", "codes = $codes")
                    codes.forEach {  barcodeRecognition ->
                        if (barcodeRecognition.info?.text != null) {
                            binding?.result?.post {
                                binding?.result?.text = barcodeRecognition.info?.text
                            }
                        }
                    }
                }
                imageProxy.image?.close()
                imageProxy.close()


            }
        }
    }
}