package com.photo.filter.detail.domain

import com.photo.filter.detail.data.remote.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) { photoRepository.getPhoto() }
}
