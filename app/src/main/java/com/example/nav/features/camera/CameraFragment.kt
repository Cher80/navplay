package com.example.nav.features.camera

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
import com.example.nav.databinding.FragmentCameraBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var binding: FragmentCameraBinding? = null

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS,
        )
        .enableAllPotentialBarcodes() // Optional
        .build()

    val scanner = BarcodeScanning.getClient(options)

    // setting up the analysis use case
    val analysisUseCase = ImageAnalysis.Builder()
        .build().apply {
            setAnalyzer(Executors.newSingleThreadExecutor(), YourImageAnalyzer())
        }
//    val analysisUseCase = YourImageAnalyzer()

//    var barCodeAnalyzer: BarCodeAnalyzer? = null

    //val viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    val viewModel by viewModels<CameraViewModel>()

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
        binding = FragmentCameraBinding.inflate(inflater, container, false)
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
        scanner.close()
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
                scanner.process(image)
                    .addOnSuccessListener { barcodeList ->
                        Log.d(TAG, "barcodeList=$barcodeList")
                        val barcode = barcodeList.getOrNull(0)
                        Log.d(TAG, "barcode=$barcode")
                        Log.d(TAG, "barcode?.rawValue=${barcode?.rawValue}")
                        // `rawValue` is the decoded value of the barcode
                        barcode?.rawValue?.let { value ->
                            // update our textView to show the decoded value
                            binding?.result?.text = value
                        }
                    }
                    .addOnFailureListener {
                        // This failure will happen if the barcode scanning model
                        // fails to download from Google Play Services
                        Log.e(TAG, it.message.orEmpty())
                        binding?.result?.text = ""
                    }.addOnCompleteListener {
                        // When the image is from CameraX analysis use case, must
                        // call image.close() on received images when finished
                        // using them. Otherwise, new images may not be received
                        // or the camera may stall.
                        imageProxy.image?.close()
                        imageProxy.close()
                    }

            }
        }
    }
}