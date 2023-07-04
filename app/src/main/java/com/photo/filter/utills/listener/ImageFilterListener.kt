package com.photo.filter.utills.listener

import com.photo.filter.detail.data.local.model.ImageFilterModel

interface ImageFilterListener {
    fun onFilterSelected(imageFilterModel: ImageFilterModel)
}