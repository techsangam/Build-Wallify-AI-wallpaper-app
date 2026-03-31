package com.wallifyai.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryOnFailureInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        var attempt = 0

        while ((response.code == 429 || response.code in 500..599) && attempt < 2) {
            val delayMillis = response.header("Retry-After")
                ?.toLongOrNull()
                ?.times(1000)
                ?.coerceAtMost(5000)
                ?: (1000L * (attempt + 1))
            response.close()
            Thread.sleep(delayMillis)
            response = chain.proceed(chain.request())
            attempt++
        }

        return response
    }
}

