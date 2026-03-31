package com.wallifyai.data.service

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperDestination
import com.wallifyai.domain.model.WallpaperScaleMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wallpaperBitmapLoader: WallpaperBitmapLoader,
) {

    suspend fun setWallpaper(
        wallpaper: Wallpaper,
        destination: WallpaperDestination,
        scaleMode: WallpaperScaleMode,
    ) = withContext(Dispatchers.IO) {
        val rawBitmap = wallpaperBitmapLoader.loadBitmap(wallpaper.fullUrl)
        val preparedBitmap = rawBitmap.prepareForWallpaper(
            targetWidth = context.resources.displayMetrics.widthPixels,
            targetHeight = context.resources.displayMetrics.heightPixels,
            scaleMode = scaleMode,
        )
        val wallpaperManager = WallpaperManager.getInstance(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when (destination) {
                WallpaperDestination.HOME -> wallpaperManager.setBitmap(
                    preparedBitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM,
                )

                WallpaperDestination.LOCK -> wallpaperManager.setBitmap(
                    preparedBitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_LOCK,
                )

                WallpaperDestination.BOTH -> {
                    wallpaperManager.setBitmap(
                        preparedBitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM,
                    )
                    wallpaperManager.setBitmap(
                        preparedBitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK,
                    )
                }
            }
        } else {
            wallpaperManager.setBitmap(preparedBitmap)
        }
    }

    private fun Bitmap.prepareForWallpaper(
        targetWidth: Int,
        targetHeight: Int,
        scaleMode: WallpaperScaleMode,
    ): Bitmap {
        val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawColor(Color.BLACK)

        val widthRatio = targetWidth / width.toFloat()
        val heightRatio = targetHeight / height.toFloat()
        val scale = when (scaleMode) {
            WallpaperScaleMode.FIT -> minOf(widthRatio, heightRatio)
            WallpaperScaleMode.CROP -> maxOf(widthRatio, heightRatio)
        }

        val scaledWidth = width * scale
        val scaledHeight = height * scale
        val left = (targetWidth - scaledWidth) / 2f
        val top = (targetHeight - scaledHeight) / 2f
        val destinationRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        canvas.drawBitmap(this, null, destinationRect, Paint(Paint.ANTI_ALIAS_FLAG))
        return output
    }
}
