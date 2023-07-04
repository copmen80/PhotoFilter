package com.photo.filter.main.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.photo.filter.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                navigateToCameraFragment()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please confirm permission to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                navigateToDetailFragment(data?.data.toString(), false)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

    }

    private fun initListeners() {
        binding.bPhotoCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                navigateToCameraFragment()
            } else {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }

        binding.bPhotoGallery.setOnClickListener {
            chooseImageGallery()
        }
        binding.bPhotoInternet.setOnClickListener {
            navigateToDetailFragment("", true)
        }
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }


    private fun navigateToDetailFragment(uri: String, isFromInternet: Boolean) {
        Log.d("NavigationCustomTag","Main")
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToDetailFragment(uri, isFromInternet)
        )
    }

    private fun navigateToCameraFragment() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToCameraFragment())
    }
}
