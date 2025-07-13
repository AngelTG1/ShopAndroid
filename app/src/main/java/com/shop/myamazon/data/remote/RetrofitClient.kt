package com.shop.myamazon.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // ✅ Agregar para debugging
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://98.83.250.156:3000/API/v1/"

    // ✅ Instancia del interceptor de autenticación
    private val authInterceptor = AuthInterceptor()

    // ✅ Interceptor de logging para debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Muestra todo: headers, body, etc.
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // ✅ Agregar logging interceptor PRIMERO
        .addInterceptor(authInterceptor)   // ✅ Luego el auth interceptor
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = retrofit.create(AuthApiService::class.java)
    val productApi: ProductApiService = retrofit.create(ProductApiService::class.java)
    val cartApi: CartApiService = retrofit.create(CartApiService::class.java)

    // ✅ Funciones para manejar el token
    fun setAuthToken(token: String?) {
        authInterceptor.setToken(token)
    }

    fun clearAuthToken() {
        authInterceptor.clearToken()
    }
}