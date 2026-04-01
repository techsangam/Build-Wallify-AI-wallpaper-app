package com.wallifyai.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WallhavenSearchResponseDto(
    @SerializedName("data")
    val data: List<WallhavenWallpaperDto> = emptyList(),
    @SerializedName("meta")
    val meta: WallhavenSearchMetaDto? = null,
)

data class WallhavenWallpaperDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("category")
    val category: String?,
    @SerializedName("dimension_x")
    val width: Int,
    @SerializedName("dimension_y")
    val height: Int,
    @SerializedName("path")
    val path: String,
    @SerializedName("thumbs")
    val thumbs: WallhavenThumbsDto,
    @SerializedName("uploader")
    val uploader: WallhavenUploaderDto? = null,
    @SerializedName("tags")
    val tags: List<WallhavenTagDto>? = null,
)

data class WallhavenThumbsDto(
    @SerializedName("small")
    val small: String?,
    @SerializedName("large")
    val large: String?,
    @SerializedName("original")
    val original: String?,
)

data class WallhavenUploaderDto(
    @SerializedName("username")
    val username: String?,
)

data class WallhavenTagDto(
    @SerializedName("name")
    val name: String?,
)

data class WallhavenSearchMetaDto(
    @SerializedName("current_page")
    val currentPage: Int? = null,
    @SerializedName("last_page")
    val lastPage: Int? = null,
    @SerializedName("per_page")
    val perPage: Int? = null,
)

