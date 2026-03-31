package com.wallifyai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UnsplashPhotoDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("alt_description")
    val altDescription: String?,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("urls")
    val urls: UnsplashUrlsDto,
    @SerializedName("user")
    val user: UnsplashUserDto,
    @SerializedName("links")
    val links: UnsplashLinksDto,
)

data class UnsplashUrlsDto(
    @SerializedName("thumb")
    val thumb: String,
    @SerializedName("regular")
    val regular: String,
    @SerializedName("full")
    val full: String,
)

data class UnsplashUserDto(
    @SerializedName("name")
    val name: String,
)

data class UnsplashLinksDto(
    @SerializedName("download_location")
    val downloadLocation: String?,
)

data class UnsplashSearchResponseDto(
    @SerializedName("results")
    val results: List<UnsplashPhotoDto>,
)

