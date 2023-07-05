package com.photo.filter.camera

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photo.filter.R
import com.photo.filter.databinding.FragmentCameraBinding
import com.photo.filter.detail.ui.model.DetailScreenArgs
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutionException


@AndroidEntryPoint
class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var cameraFacing = CameraSelector.LENS_FACING_BACK


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCameraBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        startCamera(cameraFacing)
    }

    private fun initListeners() {
        binding.ibSwitchCamera.setOnClickListener {
            cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            startCamera(cameraFacing)
        }
    }

    private fun startCamera(cameraFacing: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get() as ProcessCameraProvider

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.pvCamera.surfaceProvider)
            }

            try {
                val imageCapture =
                    ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing).build()

                cameraProvider.unbindAll()

                val camera: Camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

                binding.ibTakePhoto.setOnClickListener {
                    takePicture(imageCapture)
                }

                binding.ibFlashLight.setOnClickListener {
                    setFlashIcon(camera)
                }

            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePicture(imageCapture: ImageCapture) {

        val name = "IMG ${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoFilter-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    startCamera(cameraFacing)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d(TAG, msg)
                    navigateToCameraFragment(DetailScreenArgs.LocalSource(output.savedUri.toString()))
                }
            }
        )
    }

    private fun setFlashIcon(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                binding.ibFlashLight.setImageResource(R.drawable.ic_flashlight_off)
            } else {
                camera.cameraControl.enableTorch(false)
                binding.ibFlashLight.setImageResource(R.drawable.ic_flashlight_on)
            }
        } else {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "FlashLight is unavailable", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun navigateToCameraFragment(args: DetailScreenArgs) {
        Log.d("NavigationCustomTag", "Camera")
        findNavController().navigate(
            CameraFragmentDirections.actionCameraFragmentToDetailFragment(args)
        )
    }
}
