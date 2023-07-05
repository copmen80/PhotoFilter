package com.photo.filter.detail.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DetailScreenArgs : Parcelable {
    object NetworkSource : DetailScreenArgs()
    data class LocalSource(val uri: String) : DetailScreenArgs()
}
