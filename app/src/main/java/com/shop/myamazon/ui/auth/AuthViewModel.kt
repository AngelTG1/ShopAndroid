package com.shop.myamazon.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.myamazon.MyApplication
import com.shop.myamazon.data.remote.RetrofitClient
import com.shop.myamazon.data.remote.models.User
import com.shop.myamazon.data.remote.repository.AuthRepository
import com.shop.myamazon.ui.cart.CartViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private var cartViewModel: CartViewModel? = null

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun setCartViewModel(cartViewModel: CartViewModel) {
        this.cartViewModel = cartViewModel
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = authRepository.login(email, password)

                if (response.isSuccessful) {
                    val authResponse = response.body()

                    // ✅ Verificar que status sea "success"
                    if (authResponse?.success == true) {
                        // ✅ Establecer el token para futuras peticiones
                        authResponse.token?.let { token ->
                            authRepository.setAuthToken(token)
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            user = authResponse.user, // ← Aquí se guarda el objeto User completo
                            token = authResponse.token
                        )

                        // ✅ Log para debugging
                        println("✅ Login exitoso: ${authResponse.user?.name}")

                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = authResponse?.message ?: "Error desconocido"
                        )

                        // ✅ Log para debugging
                        println("❌ Login falló: ${authResponse?.message}")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Upss, Verifica bien si sus datos son correctos :)"
                    )

                    // ✅ Log para debugging
                    println("❌ Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )

                // ✅ Log para debugging
                println("❌ Excepción: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun register(name: String, lastName: String, email: String, password: String, phone: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = authRepository.register(name, lastName, email, password, phone)

                if (response.isSuccessful) {
                    val authResponse = response.body()

                    // ✅ Verificar que status sea "success"
                    if (authResponse?.success == true) {
                        // ✅ Establecer el token para futuras peticiones
                        authResponse.token?.let { token ->
                            authRepository.setAuthToken(token)
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            user = authResponse.user,
                            token = authResponse.token
                        )

                        // ✅ Log para debugging
                        println("✅ Registro exitoso: ${authResponse.user?.name}")

                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = authResponse?.message ?: "Error desconocido"
                        )

                        // ✅ Log para debugging
                        println("❌ Registro falló: ${authResponse?.message}")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Upss, Verifica bien si sus datos son correctos :)"
                    )

                    // ✅ Log para debugging
                    println("❌ Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )

                // ✅ Log para debugging
                println("❌ Excepción: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onLoginSuccess(token: String, user: User) {
        println("✅ Login exitoso para: ${user.name}")

        // Configurar autenticación
        RetrofitClient.setAuthToken(token)
        saveAuthData(token, user)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoggedIn = true,
            token = token,
            user = user,
            errorMessage = null
        )

        // ✅ Inicializar carrito para el nuevo usuario
        cartViewModel?.initializeForNewUser()
        println("🛒 Carrito inicializado para nuevo usuario")
    }

    fun logout() {
        // ✅ Limpiar el token al hacer logout
        authRepository.clearAuthToken()
        _uiState.value = AuthUiState()

        // ✅ Log para debugging
        println("✅ Logout realizado")
    }

    private fun saveAuthData(token: String, user: User) {
        val sharedPrefs = MyApplication.instance.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("auth_token", token)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_role", user.role)
            .apply()
    }
}