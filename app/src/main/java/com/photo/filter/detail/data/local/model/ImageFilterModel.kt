package com.photo.filter.detail.data.local.model

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

data class ImageFilterModel(
    val name: String,
    val filter: GPUImageFilter,
    val filterPreview: Bitmap
)