package com.wallifyai.data.remote.interceptor

import com.wallifyai.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashAuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept-Version", "v1")
            .apply {
                if (BuildConfig.UNSPLASH_ACCESS_KEY.isNotBlank()) {
                    addHeader("Authorization", "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}")
                }
            }
            .build()
        return chain.proceed(request)
    }
}

