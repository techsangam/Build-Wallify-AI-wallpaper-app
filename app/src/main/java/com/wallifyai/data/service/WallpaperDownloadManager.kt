package com.wallifyai.data.service

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.wallifyai.domain.model.Wallpaper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wallpaperBitmapLoader: WallpaperBitmapLoader,
) {

    suspend fun downloadWallpaper(wallpaper: Wallpaper): Uri = withContext(Dispatchers.IO) {
        val bitmap = wallpaperBitmapLoader.loadBitmap(wallpaper.fullUrl)
        val fileName = "wallify_${wallpaper.id}_${System.currentTimeMillis()}.jpg"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/WallifyAI",
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values,
            ) ?: error("Unable to create MediaStore entry.")

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                check(bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    "Unable to save the wallpaper."
                }
            } ?: error("Unable to open wallpaper output stream.")

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            uri
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val outputDir = File(picturesDir, "WallifyAI").apply { mkdirs() }
            val file = File(outputDir, fileName)

            FileOutputStream(file).use { outputStream ->
                check(bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    "Unable to save the wallpaper."
                }
            }

            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null,
            )

            Uri.fromFile(file)
        }
    }
}
