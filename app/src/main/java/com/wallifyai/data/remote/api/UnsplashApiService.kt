package com.wallifyai.data.remote.api

import com.wallifyai.data.remote.dto.UnsplashPhotoDto
import com.wallifyai.data.remote.dto.UnsplashSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String = "popular",
    ): List<UnsplashPhotoDto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String = "relevant",
    ): UnsplashSearchResponseDto
}

