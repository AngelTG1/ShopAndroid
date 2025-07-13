package com.shop.myamazon.data.remote

import com.shop.myamazon.data.remote.models.AuthResponse
import com.shop.myamazon.data.remote.models.LoginRequest
import com.shop.myamazon.data.remote.models.RegisterRequest
import com.shop.myamazon.data.remote.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

}