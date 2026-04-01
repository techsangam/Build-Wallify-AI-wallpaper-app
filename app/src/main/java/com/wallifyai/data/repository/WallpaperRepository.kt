package com.wallifyai.data.repository

import com.wallifyai.data.local.dao.CachedWallpaperDao
import com.wallifyai.data.local.dao.FavoriteWallpaperDao
import com.wallifyai.data.local.dao.UserActivityDao
import com.wallifyai.data.local.entity.UserActivityEntity
import com.wallifyai.data.mapper.toCacheEntity
import com.wallifyai.data.mapper.toDomain
import com.wallifyai.data.mapper.toFavoriteEntity
import com.wallifyai.data.remote.api.WallhavenApiService
import com.wallifyai.domain.model.UserActionType
import com.wallifyai.domain.model.Wallpaper
import com.wallifyai.domain.model.WallpaperCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRepository @Inject constructor(
    private val apiService: WallhavenApiService,
    private val cachedWallpaperDao: CachedWallpaperDao,
    private val favoriteWallpaperDao: FavoriteWallpaperDao,
    private val userActivityDao: UserActivityDao,
) {

    suspend fun getWallpapers(
        category: WallpaperCategory,
        page: Int,
        perPage: Int = DEFAULT_PAGE_SIZE,
    ): List<Wallpaper> = withContext(Dispatchers.IO) {
        runCatching {
            val items = apiService.searchWallpapers(
                query = category.apiQuery,
                page = page,
                categories = DEFAULT_CATEGORIES,
                purity = DEFAULT_PURITY,
                sorting = if (category == WallpaperCategory.ALL) "toplist" else "relevance",
                order = "desc",
                topRange = if (category == WallpaperCategory.ALL) "1M" else null,
                minimumResolution = DEFAULT_MINIMUM_RESOLUTION,
            ).data
                .map { it.toDomain(category) }
                .take(perPage)

            cachedWallpaperDao.upsertAll(items.map { it.toCacheEntity(page) })
            items
        }.getOrElse {
            cachedWallpaperDao.getByCategoryAndPage(category.storageKey, page).map { entity ->
                entity.toDomain()
            }
        }
    }

    suspend fun getRecommendedWallpapers(limit: Int = 8): List<Wallpaper> = withContext(Dispatchers.IO) {
        val topCategories = userActivityDao.getTopCategories(limit = 3)
            .map { WallpaperCategory.fromStorageKey(it.category) }
            .filter { it != WallpaperCategory.ALL }
            .ifEmpty { listOf(WallpaperCategory.NATURE, WallpaperCategory.TECH) }

        val remoteItems = topCategories.flatMap { category ->
            runCatching {
                getWallpapers(
                    category = category,
                    page = 1,
                    perPage = DEFAULT_PAGE_SIZE,
                )
            }.getOrElse {
                cachedWallpaperDao.getLatestByCategory(category.storageKey, DEFAULT_PAGE_SIZE).map { entity ->
                    entity.toDomain()
                }
            }
        }

        remoteItems.distinctBy { it.id }.take(limit)
    }

    fun observeFavorites(): Flow<List<Wallpaper>> {
        return favoriteWallpaperDao.observeAll().map { favorites ->
            favorites.map { it.toDomain() }
        }
    }

    fun observeFavoriteIds(): Flow<Set<String>> {
        return favoriteWallpaperDao.observeFavoriteIds().map { it.toSet() }
    }

    fun observeIsFavorite(imageId: String): Flow<Boolean> {
        return favoriteWallpaperDao.observeFavorite(imageId).map { it != null }
    }

    suspend fun toggleFavorite(wallpaper: Wallpaper): Boolean = withContext(Dispatchers.IO) {
        val isFavorite = favoriteWallpaperDao.isFavorite(wallpaper.id)
        if (isFavorite) {
            favoriteWallpaperDao.deleteById(wallpaper.id)
            false
        } else {
            favoriteWallpaperDao.upsert(wallpaper.toFavoriteEntity())
            recordActivity(wallpaper, UserActionType.FAVORITE)
            true
        }
    }

    suspend fun recordActivity(
        wallpaper: Wallpaper,
        actionType: UserActionType,
    ) = withContext(Dispatchers.IO) {
        userActivityDao.insert(
            UserActivityEntity(
                imageId = wallpaper.id,
                category = wallpaper.category,
                actionType = actionType.name,
                timestamp = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun clearCachedMetadata() = withContext(Dispatchers.IO) {
        cachedWallpaperDao.clearAll()
    }

    companion object {
        private const val DEFAULT_CATEGORIES = "111"
        private const val DEFAULT_PURITY = "100"
        private const val DEFAULT_MINIMUM_RESOLUTION = "1920x1080"

        const val DEFAULT_PAGE_SIZE = 24
    }
}
