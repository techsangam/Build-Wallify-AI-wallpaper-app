package com.wallifyai.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoWallpaperScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun schedule(hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        val nextRun = now.withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)
            .let { candidate ->
                if (candidate.isAfter(now)) candidate else candidate.plusDays(1)
            }

        val initialDelayMillis = Duration.between(now, nextRun)
            .toMillis()
            .coerceAtLeast(TimeUnit.MINUTES.toMillis(1))

        val request = PeriodicWorkRequestBuilder<AutoWallpaperWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            AUTO_WALLPAPER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(AUTO_WALLPAPER_WORK_NAME)
    }

    companion object {
        const val AUTO_WALLPAPER_WORK_NAME = "wallify_auto_wallpaper"
    }
}
