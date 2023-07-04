package com.photo.filter.detail.data.remote.response

import com.google.gson.annotations.SerializedName


data class PhotoResponse(
    @field:SerializedName("urls")
    val urls: Urls,
)

data class Urls(
    @field:SerializedName("raw")
    val raw: String,
)

