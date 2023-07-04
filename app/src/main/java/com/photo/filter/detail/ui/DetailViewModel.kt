package com.photo.filter.detail.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photo.filter.detail.domain.GetFiltersUseCase
import com.photo.filter.detail.domain.GetPhotoUseCase
import com.photo.filter.detail.domain.mapper.PhotoResponseToPhotoUiModelMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//sealed class PhotoDataState {
//    object IsLoading : PhotoDataState()
//    data class Success(val photoResponse: PhotoResponse) : PhotoDataState()
//    object Error : PhotoDataState()
//
//}

@HiltViewModel
class DetailViewModel @Inject constructor(
    application: Application,
    private val getPhotoUseCase: GetPhotoUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val photoUiModelMapper: PhotoResponseToPhotoUiModelMapper
) : AndroidViewModel(application) {

//    val photoDataState = MutableStateFlow<PhotoDataState>(PhotoDataState.IsLoading)

    private val eventChannel = Channel<DetailEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getPhotoFromInternet() {
        viewModelScope.launch {
            getPhotoUseCase()
                .onSuccess {
                    eventChannel.send(DetailEvent.PhotoReceived(photoUiModelMapper.map(it)))
                }.onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun loadImageFilters(originalImage: Bitmap) {
        viewModelScope.launch {
            eventChannel.send(DetailEvent.FiltersReceived(getFiltersUseCase(originalImage)))
        }
    }
}
