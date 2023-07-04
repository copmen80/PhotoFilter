package com.photo.filter.detail.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.photo.filter.databinding.FragmentDetailBinding
import com.photo.filter.detail.data.local.model.ImageFilterModel
import com.photo.filter.detail.ui.adapter.FilterAdapter
import com.photo.filter.utills.listener.ImageFilterListener
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment(), ImageFilterListener {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailViewModel>()

    private val uri: String by lazy { requireNotNull(arguments?.getString(URI_KEY)) }
    private val isFromInternet: Boolean by lazy { requireNotNull(arguments?.getBoolean(INTERNET_KEY)) }

    private lateinit var gpuImage: GPUImage

    private lateinit var originalBitmap: Bitmap
    private val filteredBitmap = MutableStateFlow(originalBitmap)

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

        if (!isFromInternet) {

            Glide.with(this@DetailFragment)
                .load(uri)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        binding.ivDetail.setImageDrawable(resource)
                        viewModel.loadImageFilters(resource.toBitmap())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            /* Glide
                 .with(this@DetailFragment)
                 .load(uri.toUri())
                 .into(binding.ivDetail)
             viewModel.loadImageFilters(binding.ivDetail.drawable.toBitmap())*/
        } else {
            viewModel.getPhotoFromInternet()
        }

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
                                        originalBitmap = bitmap
                                        filteredBitmap.value = bitmap

                                        gpuImage.setImage(bitmap)

                                        binding.ivDetail.setImageDrawable(resource)
                                        viewModel.loadImageFilters(bitmap)
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })
                        }
                        is DetailEvent.FiltersReceived -> {
                            FilterAdapter(this@DetailFragment, event.filter).also { adapter ->
                                binding.rvFilters.layoutManager =
                                    LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                binding.rvFilters.adapter = adapter
                            }
                            filteredBitmap.collect { bitmap ->
                                binding.ivDetail.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFilterSelected(imageFilterModel: ImageFilterModel) {
        with(imageFilterModel) {
            with(gpuImage) {
                setFilter(filter)
                filteredBitmap.value = bitmapWithFilterApplied
            }
        }
    }

    companion object {
        private const val URI_KEY = "uri"
        private const val INTERNET_KEY = "isFromInternet"
    }
}
