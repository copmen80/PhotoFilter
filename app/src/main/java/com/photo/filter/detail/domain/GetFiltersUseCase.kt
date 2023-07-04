package com.photo.filter.detail.domain

import android.graphics.Bitmap
import com.photo.filter.detail.data.local.FilterRepository
import javax.inject.Inject

class GetFiltersUseCase @Inject constructor(
    private val filterRepository: FilterRepository
) {
    suspend operator fun invoke(image: Bitmap) = filterRepository.getImageFilters(image)
}