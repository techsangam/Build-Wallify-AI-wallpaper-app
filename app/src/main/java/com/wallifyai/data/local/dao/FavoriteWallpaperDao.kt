package com.wallifyai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wallifyai.data.local.entity.FavoriteWallpaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteWallpaperDao {

    @Query("SELECT * FROM favorite_wallpapers ORDER BY added_at DESC")
    fun observeAll(): Flow<List<FavoriteWallpaperEntity>>

    @Query("SELECT image_id FROM favorite_wallpapers")
    fun observeFavoriteIds(): Flow<List<String>>

    @Query("SELECT * FROM favorite_wallpapers WHERE image_id = :imageId LIMIT 1")
    fun observeFavorite(imageId: String): Flow<FavoriteWallpaperEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_wallpapers WHERE image_id = :imageId)")
    suspend fun isFavorite(imageId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FavoriteWallpaperEntity)

    @Query("DELETE FROM favorite_wallpapers WHERE image_id = :imageId")
    suspend fun deleteById(imageId: String)
}

