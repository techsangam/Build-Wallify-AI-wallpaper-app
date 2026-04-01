package com.wallifyai.data.remote.api

import com.wallifyai.data.remote.dto.WallhavenSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WallhavenApiService {

    @GET("search")
    suspend fun searchWallpapers(
        @Query("q") query: String? = null,
        @Query("page") page: Int,
        @Query("categories") categories: String = "111",
        @Query("purity") purity: String = "100",
        @Query("sorting") sorting: String = "toplist",
        @Query("order") order: String = "desc",
        @Query("topRange") topRange: String? = null,
        @Query("atleast") minimumResolution: String = "1920x1080",
    ): WallhavenSearchResponseDto
}
