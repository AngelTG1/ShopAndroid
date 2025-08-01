package com.shop.shop.data.remote

import com.shop.shop.data.remote.models.AuthResponse
import com.shop.shop.data.remote.models.LoginRequest
import com.shop.shop.data.remote.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

}