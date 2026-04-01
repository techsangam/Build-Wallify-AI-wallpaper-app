package com.wallifyai.data.remote.interceptor

import com.wallifyai.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallhavenAuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .apply {
                if (BuildConfig.WALLHAVEN_API_KEY.isNotBlank()) {
                    addHeader("X-API-Key", BuildConfig.WALLHAVEN_API_KEY)
                }
            }
            .build()
        return chain.proceed(request)
    }
}
