package com.wallifyai.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_wallpapers")
data class FavoriteWallpaperEntity(
    @PrimaryKey
    @ColumnInfo(name = "image_id")
    val imageId: String,
    val category: String,
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
    @ColumnInfo(name = "added_at")
    val addedAt: Long,
)

