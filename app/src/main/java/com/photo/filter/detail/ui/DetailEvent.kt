package com.photo.filter.detail.ui

import com.photo.filter.detail.data.local.model.ImageFilterModel
import com.photo.filter.detail.ui.model.PhotoUiModel

sealed class DetailEvent {
    data class PhotoReceived(val photoUiModel: PhotoUiModel) : DetailEvent()
    data class FiltersReceived(val filter: List<ImageFilterModel>) : DetailEvent()
}