package com.photo.filter.detail.domain

import android.graphics.Bitmap
import com.photo.filter.detail.data.local.LocalPhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFiltersUseCase @Inject constructor(
    private val localphotoRepository: LocalPhotoRepository
) {
    suspend operator fun invoke(image: Bitmap) = withContext(Dispatchers.IO) {
        localphotoRepository.getImageFilters(image)
    }
}