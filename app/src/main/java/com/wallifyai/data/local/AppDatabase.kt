package com.wallifyai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wallifyai.data.local.dao.CachedWallpaperDao
import com.wallifyai.data.local.dao.FavoriteWallpaperDao
import com.wallifyai.data.local.dao.UserActivityDao
import com.wallifyai.data.local.entity.CachedWallpaperEntity
import com.wallifyai.data.local.entity.FavoriteWallpaperEntity
import com.wallifyai.data.local.entity.UserActivityEntity

@Database(
    entities = [
        UserActivityEntity::class,
        FavoriteWallpaperEntity::class,
        CachedWallpaperEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userActivityDao(): UserActivityDao
    abstract fun favoriteWallpaperDao(): FavoriteWallpaperDao
    abstract fun cachedWallpaperDao(): CachedWallpaperDao
}

