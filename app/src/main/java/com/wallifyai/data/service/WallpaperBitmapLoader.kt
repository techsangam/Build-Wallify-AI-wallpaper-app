package com.wallifyai.data.service

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperBitmapLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) {

    suspend fun loadBitmap(imageUrl: String): Bitmap = withContext(Dispatchers.IO) {
        val result = imageLoader.execute(
            ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
        )

        val drawable = result.drawable ?: error("Failed to decode image.")
        drawable.toBitmap()
    }
}
