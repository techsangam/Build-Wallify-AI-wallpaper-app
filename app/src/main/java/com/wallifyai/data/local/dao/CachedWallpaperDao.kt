package com.wallifyai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wallifyai.data.local.entity.CachedWallpaperEntity

@Dao
interface CachedWallpaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedWallpaperEntity>)

    @Query("SELECT * FROM cached_wallpapers WHERE category = :category AND page = :page ORDER BY cached_at DESC")
    suspend fun getByCategoryAndPage(category: String, page: Int): List<CachedWallpaperEntity>

    @Query("SELECT * FROM cached_wallpapers WHERE category = :category ORDER BY page ASC, cached_at DESC LIMIT :limit")
    suspend fun getLatestByCategory(category: String, limit: Int): List<CachedWallpaperEntity>

    @Query("DELETE FROM cached_wallpapers")
    suspend fun clearAll()
}

