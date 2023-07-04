package com.photo.filter.detail.domain.mapper

import com.photo.filter.detail.data.remote.response.PhotoResponse
import com.photo.filter.detail.ui.model.PhotoUiModel
import javax.inject.Inject

class PhotoResponseToPhotoUiModelMapper @Inject constructor() {

    fun map(photoResponse: PhotoResponse): PhotoUiModel {
        return PhotoUiModel(
            url = photoResponse.urls.raw
        )
    }
}