package com.shop.shop.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    @Volatile
    private var token: String? = null

    fun setToken(newToken: String?) {
        token = newToken
    }

    fun clearToken() {
        token = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si no hay token, continúa con la petición normal
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Agregar el Bearer token al header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}