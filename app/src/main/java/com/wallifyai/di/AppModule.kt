package com.wallifyai.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.request.CachePolicy
import com.wallifyai.BuildConfig
import com.wallifyai.data.local.AppDatabase
import com.wallifyai.data.local.dao.CachedWallpaperDao
import com.wallifyai.data.local.dao.FavoriteWallpaperDao
import com.wallifyai.data.local.dao.UserActivityDao
import com.wallifyai.data.remote.api.WallhavenApiService
import com.wallifyai.data.remote.interceptor.RetryOnFailureInterceptor
import com.wallifyai.data.remote.interceptor.WallhavenAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: WallhavenAuthInterceptor,
        retryOnFailureInterceptor: RetryOnFailureInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(retryOnFailureInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideWallhavenApiService(okHttpClient: OkHttpClient): WallhavenApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.WALLHAVEN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WallhavenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wallify_ai.db",
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserActivityDao(database: AppDatabase): UserActivityDao = database.userActivityDao()

    @Provides
    fun provideFavoriteWallpaperDao(database: AppDatabase): FavoriteWallpaperDao =
        database.favoriteWallpaperDao()

    @Provides
    fun provideCachedWallpaperDao(database: AppDatabase): CachedWallpaperDao =
        database.cachedWallpaperDao()

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)
            .build()
    }
}
