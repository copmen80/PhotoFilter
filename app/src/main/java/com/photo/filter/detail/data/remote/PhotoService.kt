package com.photo.filter.detail.data.remote

import com.photo.filter.detail.data.remote.response.PhotoResponse
import retrofit2.http.GET

interface PhotoService {
    @GET("/photos/random")
    suspend fun getPhoto(): PhotoResponse
}