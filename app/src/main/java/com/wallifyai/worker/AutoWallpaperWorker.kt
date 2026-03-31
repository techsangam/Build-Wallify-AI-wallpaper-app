package com.wallifyai.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wallifyai.data.repository.SettingsRepository
import com.wallifyai.data.repository.WallpaperRepository
import com.wallifyai.data.service.WallpaperManagerHelper
import com.wallifyai.domain.model.WallpaperDestination
import com.wallifyai.domain.model.WallpaperScaleMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AutoWallpaperWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val wallpaperManagerHelper: WallpaperManagerHelper,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val settings = settingsRepository.settingsFlow.first()
        if (!settings.autoWallpaperEnabled) {
            return Result.success()
        }

        val wallpapers = wallpaperRepository.getWallpapers(
            category = settings.autoWallpaperCategory,
            page = 1,
            perPage = 12,
        )

        val nextWallpaper = wallpapers.randomOrNull() ?: return Result.retry()

        return runCatching {
            wallpaperManagerHelper.setWallpaper(
                wallpaper = nextWallpaper,
                destination = WallpaperDestination.BOTH,
                scaleMode = WallpaperScaleMode.CROP,
            )
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}
