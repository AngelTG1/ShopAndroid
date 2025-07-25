package com.shop.shop.data.remote.repository

import com.shop.shop.data.remote.RetrofitClient
import com.shop.shop.data.remote.models.AuthResponse
import com.shop.shop.data.remote.models.LoginRequest
import com.shop.shop.data.remote.models.RegisterRequest
import retrofit2.Response

class AuthRepository {
    private val authApi = RetrofitClient.authApi

    suspend fun login(email: String, password: String): Response<AuthResponse> {
        return authApi.login(LoginRequest(email, password))
    }

    suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        phone: String?
    ): Response<AuthResponse> {
        return authApi.register(
            RegisterRequest(
                name = name,
                lastName = lastName,
                email = email,
                password = password,
                phone = phone,
                role = "Cliente"
            )
        )
    }

    // ✅ Función para establecer el token
    fun setAuthToken(token: String?) {
        RetrofitClient.setAuthToken(token)
    }

    // ✅ Función para limpiar el token
    fun clearAuthToken() {
        RetrofitClient.clearAuthToken()
    }
}