package com.photo.filter.detail.ui

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.photo.filter.detail.domain.GetFiltersUseCase
import com.photo.filter.detail.domain.GetPhotoUseCase
import com.photo.filter.detail.domain.SaveFilteredImageUseCase
import com.photo.filter.detail.domain.mapper.PhotoResponseToPhotoUiModelMapper
import com.photo.filter.detail.ui.model.DetailScreenArgs
import com.photo.filter.detail.ui.model.PhotoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    application: Application,
    private val getPhotoUseCase: GetPhotoUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val saveFilteredImageUseCase: SaveFilteredImageUseCase,
    private val photoUiModelMapper: PhotoResponseToPhotoUiModelMapper
) : AndroidViewModel(application) {

    private val eventChannel = Channel<DetailEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun loadImageFilters(originalImage: Bitmap) {
        viewModelScope.launch {
            eventChannel.send(DetailEvent.FiltersReceived(getFiltersUseCase(originalImage)))
        }
    }

    fun saveFilteredImage(filteredBitmap: Bitmap) {
        viewModelScope.launch {
            saveFilteredImageUseCase(filteredBitmap)
            eventChannel.send(DetailEvent.SaveFilteredImage)
        }
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

    fun initSource(args: DetailScreenArgs) {
        viewModelScope.launch {
            eventChannel.send(DetailEvent.ShowLoading)
            when (args) {
                is DetailScreenArgs.LocalSource -> {
                    eventChannel.send(
                        DetailEvent.PhotoReceived(PhotoUiModel(args.uri))
                    )
                }
                DetailScreenArgs.NetworkSource -> {
                    getPhotoUseCase()
                        .onSuccess { photoResponse ->
                            eventChannel.send(
                                DetailEvent.PhotoReceived(photoUiModelMapper.map(photoResponse))
                            )
                        }.onFailure {
                            Log.d(javaClass.simpleName, it.message, it)
                        }
                }
            }
            eventChannel.send(DetailEvent.HideLoading)
        }
    }
}
