package com.photo.filter.detail.domain

import android.graphics.Bitmap
import com.photo.filter.detail.data.local.LocalPhotoRepository
import javax.inject.Inject

class SaveFilteredImageUseCase @Inject constructor(
    private val localPhotoRepository: LocalPhotoRepository
) {
     operator fun invoke(image: Bitmap) = localPhotoRepository.saveFilteredImage(image)
}