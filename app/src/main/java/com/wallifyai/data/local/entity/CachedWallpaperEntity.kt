package com.wallifyai.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_wallpapers")
data class CachedWallpaperEntity(
    @PrimaryKey
    @ColumnInfo(name = "cache_id")
    val cacheId: String,
    @ColumnInfo(name = "image_id")
    val imageId: String,
    val category: String,
    val page: Int,
    val description: String,
    @ColumnInfo(name = "regular_url")
    val regularUrl: String,
    @ColumnInfo(name = "full_url")
    val fullUrl: String,
    @ColumnInfo(name = "thumb_url")
    val thumbUrl: String,
    @ColumnInfo(name = "author_name")
    val authorName: String,
    val width: Int,
    val height: Int,
    @ColumnInfo(name = "download_location")
    val downloadLocation: String?,
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,
)

