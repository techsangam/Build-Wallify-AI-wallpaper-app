package com.wallifyai.data.mapper

import com.wallifyai.data.local.entity.CachedWallpaperEntity
import com.wallifyai.data.local.entity.FavoriteWallpaperEntity
import com.wallifyai.data.remote.dto.UnsplashPhotoDto
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperCategory

fun UnsplashPhotoDto.toDomain(category: WallpaperCategory): Wallpaper {
    return Wallpaper(
        id = id,
        category = category.storageKey,
        description = description ?: altDescription ?: "${category.title} wallpaper",
        regularUrl = urls.regular,
        fullUrl = urls.full,
        thumbUrl = urls.thumb,
        authorName = user.name,
        width = width,
        height = height,
        downloadLocation = links.downloadLocation,
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

