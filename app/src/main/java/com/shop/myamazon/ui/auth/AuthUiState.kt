package com.shop.myamazon.ui.auth

import com.shop.myamazon.data.remote.models.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val token: String? = null
)