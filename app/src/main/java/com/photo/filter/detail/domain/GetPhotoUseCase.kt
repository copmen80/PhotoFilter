package com.photo.filter.detail.domain

import com.photo.filter.detail.data.remote.PhotoRepository
import javax.inject.Inject

class GetPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
) {
    suspend operator fun invoke() = photoRepository.getPhoto()
}
