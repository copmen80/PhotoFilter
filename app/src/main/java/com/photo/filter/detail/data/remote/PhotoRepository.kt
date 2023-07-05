package com.photo.filter.detail.data.remote

import com.photo.filter.detail.data.remote.response.PhotoResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val photoService: PhotoService,
) {
    suspend fun getPhoto(): Result<PhotoResponse> {
        return try {
            Result.success(photoService.getPhoto())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
