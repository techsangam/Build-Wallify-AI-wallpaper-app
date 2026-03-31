package com.wallifyai.data.service

import coil.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCacheManager @Inject constructor(
    private val imageLoader: ImageLoader,
) {

    suspend fun clearCaches() = withContext(Dispatchers.IO) {
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }
}
