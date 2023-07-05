package com.photo.filter.detail.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.photo.filter.R
import com.photo.filter.databinding.FragmentDetailBinding
import com.photo.filter.detail.data.local.model.ImageFilterModel
import com.photo.filter.detail.ui.adapter.FilterAdapter
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailViewModel>()

    private val gpuImage: GPUImage by lazy { GPUImage(requireContext()) }

    private val filteredBitmap = MutableStateFlow<Bitmap?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentDetailBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleEvents()
        initToolbar()
        viewModel.initSource(requireNotNull(arguments?.getParcelable(ARGS_KEY)))
    }

    private fun initToolbar() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.bSave -> {
                        filteredBitmap.value?.let {
                            viewModel.saveFilteredImage(it)
                        }
                        Toast.makeText(requireContext(), "Image Saved", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.bShare -> {
                        with(Intent(Intent.ACTION_SEND)) {
                            putExtra(
                                Intent.EXTRA_STREAM,
                                getImageUri(requireContext(), binding.ivDetail.drawable.toBitmap())
                            )
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = "image/*"
                            startActivity(this)
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Title")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        val contentResolver = inContext.contentResolver
        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    inImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri
    }

    private fun handleEvents() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is DetailEvent.PhotoReceived -> {
                            Glide.with(this@DetailFragment)
                                .load(event.photoUiModel.url)
                                .into(object : CustomTarget<Drawable>() {
                                    override fun onResourceReady(
                                        resource: Drawable,
                                        transition: Transition<in Drawable>?
                                    ) {
                                        val bitmap = resource.toBitmap()
                                        filteredBitmap.value = bitmap

                                        gpuImage.setImage(bitmap)

                                        binding.ivDetail.setImageDrawable(resource)
                                        viewModel.loadImageFilters(bitmap)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }
                                })
                        }
                        is DetailEvent.FiltersReceived -> initFiltersAdapter(event.filter)
                        DetailEvent.HideLoading -> hideLoading()
                        DetailEvent.ShowLoading -> showLoading()
                        is DetailEvent.SaveFilteredImage -> {
                            navigateToMainFragment()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.ivDetail.visibility = View.GONE
        binding.pbImageLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.ivDetail.visibility = View.VISIBLE
        binding.pbImageLoading.visibility = View.GONE
    }

    private suspend fun initFiltersAdapter(filters: List<ImageFilterModel>) {
        binding.rvFilters.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilters.adapter = FilterAdapter(filters, ::onFilterSelected)

        filteredBitmap.collect { bitmap ->
            binding.ivDetail.setImageBitmap(bitmap)
        }
    }

    private fun onFilterSelected(imageFilterModel: ImageFilterModel) {
        with(imageFilterModel) {
            with(gpuImage) {
                setFilter(filter)
                filteredBitmap.value = bitmapWithFilterApplied
            }
        }
    }

    private fun navigateToMainFragment() {
        findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToMainFragment())
    }

    companion object {
        private const val ARGS_KEY = "args"
    }
}
