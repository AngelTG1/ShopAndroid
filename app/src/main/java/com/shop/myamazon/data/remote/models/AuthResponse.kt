package com.shop.myamazon.data.remote.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: AuthData?
) {
    // Helper property para compatibilidad
    val success: Boolean get() = status == "success"
    val user: User? get() = data?.user
    val token: String? get() = data?.token
}

data class AuthData(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("role") val role: String
)