package com.wallifyai.data.mapper

import com.wallifyai.data.local.entity.CachedWallpaperEntity
import com.wallifyai.data.local.entity.FavoriteWallpaperEntity
import com.wallifyai.data.remote.dto.WallhavenWallpaperDto
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperCategory

fun WallhavenWallpaperDto.toDomain(category: WallpaperCategory): Wallpaper {
    val fallbackTitle = when {
        category != WallpaperCategory.ALL -> "${category.title} wallpaper"
        !this.category.isNullOrBlank() -> "${this.category.replaceFirstChar { it.titlecase() }} wallpaper"
        else -> "Wallpaper"
    }
    val tagSummary = tags.orEmpty().mapNotNull { it.name }
        .filter { it.isNotBlank() }
        .take(3)
        .joinToString(separator = " / ")

    return Wallpaper(
        id = id,
        category = category.storageKey,
        description = tagSummary.ifBlank { fallbackTitle },
        regularUrl = thumbs.large ?: path,
        fullUrl = path,
        thumbUrl = thumbs.small ?: thumbs.large ?: path,
        authorName = uploader?.username?.takeUnless { it.isBlank() } ?: "Wallhaven",
        width = width,
        height = height,
        downloadLocation = path,
    )
}

fun Wallpaper.toFavoriteEntity(): FavoriteWallpaperEntity {
    return FavoriteWallpaperEntity(
        imageId = id,
        category = category,
        description = description,
        regularUrl = regularUrl,
        fullUrl = fullUrl,
        thumbUrl = thumbUrl,
        authorName = authorName,
        width = width,
        height = height,
        downloadLocation = downloadLocation,
        addedAt = System.currentTimeMillis(),
    )
}

fun FavoriteWallpaperEntity.toDomain(): Wallpaper {
    return Wallpaper(
        id = imageId,
        category = category,
        description = description,
        regularUrl = regularUrl,
        fullUrl = fullUrl,
        thumbUrl = thumbUrl,
        authorName = authorName,
        width = width,
        height = height,
        downloadLocation = downloadLocation,
    )
}

fun Wallpaper.toCacheEntity(page: Int): CachedWallpaperEntity {
    return CachedWallpaperEntity(
        cacheId = "${category}_${page}_$id",
        imageId = id,
        category = category,
        page = page,
        description = description,
        regularUrl = regularUrl,
        fullUrl = fullUrl,
        thumbUrl = thumbUrl,
        authorName = authorName,
        width = width,
        height = height,
        downloadLocation = downloadLocation,
        cachedAt = System.currentTimeMillis(),
    )
}

fun CachedWallpaperEntity.toDomain(): Wallpaper {
    return Wallpaper(
        id = imageId,
        category = category,
        description = description,
        regularUrl = regularUrl,
        fullUrl = fullUrl,
        thumbUrl = thumbUrl,
        authorName = authorName,
        width = width,
        height = height,
        downloadLocation = downloadLocation,
    )
}


