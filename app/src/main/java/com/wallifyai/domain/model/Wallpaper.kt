package com.wallifyai.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wallpaper(
    val id: String,
    val category: String,
    val description: String,
    val regularUrl: String,
    val fullUrl: String,
    val thumbUrl: String,
    val authorName: String,
    val width: Int,
    val height: Int,
    val downloadLocation: String? = null,
) : Parcelable

