package com.shop.shop.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shop.data.remote.models.Cart
import com.shop.shop.data.remote.models.CartResponse
import com.shop.shop.data.remote.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: Cart? = null,
    val cartCount: Int = 0,
    val totalAmount: Double = 0.0,
    val errorMessage: String? = null,
    val actionInProgress: Boolean = false,
    val successMessage: String? = null
)

class CartViewModel : ViewModel() {
    private val cartRepository = CartRepository()

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun resetCartState() {
        _uiState.value = CartUiState()
    }

    fun initializeForNewUser() {
        resetCartState()
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = cartRepository.getCart()

                if (response.isSuccessful) {
                    val cartResponse = response.body()

                    if (cartResponse?.status == "success") {
                        val cart = cartResponse.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            cart = cart,
                            cartCount = cart?.totalItems ?: 0,
                            totalAmount = cart?.totalAmount ?: 0.0
                        )
                    } else {
                        handleError(cartResponse?.message ?: "Error desconocido")
                    }
                } else {
                    handleHttpError(response.code())
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun loadCartCount() {
        viewModelScope.launch {
            try {
                val response = cartRepository.getCartCount()

                if (response.isSuccessful) {
                    val countResponse = response.body()
                    if (countResponse?.status == "success") {
                        val count = countResponse.data?.count ?: 0
                        val total = countResponse.data?.totalAmount ?: 0.0

                        _uiState.value = _uiState.value.copy(
                            cartCount = count,
                            totalAmount = total
                        )
                    } else {
                        resetCartCounts()
                    }
                } else {
                    resetCartCounts()
                }
            } catch (e: Exception) {
                resetCartCounts()
            }
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                actionInProgress = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = cartRepository.addToCart(productId, quantity)

                if (response.isSuccessful) {
                    val cartResponse = response.body()

                    if (cartResponse?.status == "success") {
                        val cart = cartResponse.data
                        _uiState.value = _uiState.value.copy(
                            actionInProgress = false,
                            cart = cart,
                            cartCount = cart?.totalItems ?: 0,
                            totalAmount = cart?.totalAmount ?: 0.0,
                            successMessage = "Producto agregado al carrito"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            actionInProgress = false,
                            errorMessage = cartResponse?.message ?: "Error al agregar producto"
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Datos inválidos"
                        404 -> "Producto no encontrado"
                        409 -> "Stock insuficiente"
                        401 -> "Debes iniciar sesión para agregar productos al carrito"
                        else -> "Error al agregar al carrito"
                    }

                    _uiState.value = _uiState.value.copy(
                        actionInProgress = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    actionInProgress = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun updateItemQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(itemId)
            return
        }

        viewModelScope.launch {
            performCartAction {
                cartRepository.updateCartItem(itemId, quantity)
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            performCartAction {
                cartRepository.removeFromCart(itemId)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            performCartAction {
                cartRepository.clearCart()
            }
        }
    }

    fun refreshCart() {
        loadCart()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    // ✅ CORREGIDO: Función helper con tipo simplificado
    private suspend fun performCartAction(action: suspend () -> Response<CartResponse>) {
        _uiState.value = _uiState.value.copy(
            actionInProgress = true,
            errorMessage = null
        )

        try {
            val response = action()

            if (response.isSuccessful) {
                val cartResponse = response.body()

                if (cartResponse?.status == "success") {
                    val cart = cartResponse.data
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = false,
                        cart = cart,
                        cartCount = cart?.totalItems ?: 0,
                        totalAmount = cart?.totalAmount ?: 0.0,
                        successMessage = "Operación realizada exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = false,
                        errorMessage = cartResponse?.message ?: "Error en la operación"
                    )
                }
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Datos inválidos"
                    401 -> "Sesión expirada"
                    404 -> "Elemento no encontrado"
                    409 -> "Conflicto en la operación"
                    else -> "Error en la operación: ${response.code()}"
                }

                _uiState.value = _uiState.value.copy(
                    actionInProgress = false,
                    errorMessage = errorMsg
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                actionInProgress = false,
                errorMessage = "Error de conexión: ${e.message}"
            )
        }
    }

    // ✅ Funciones helper simplificadas
    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            cart = null,
            cartCount = 0,
            totalAmount = 0.0,
            errorMessage = message
        )
    }

    private fun handleHttpError(code: Int) {
        val errorMsg = when (code) {
            401 -> "Sesión expirada, inicia sesión nuevamente"
            403 -> "No tienes permisos para acceder al carrito"
            404 -> "Carrito no encontrado"
            500 -> "Error del servidor"
            else -> "Error al cargar carrito: $code"
        }
        handleError(errorMsg)
    }

    private fun handleException(e: Exception) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            cart = null,
            cartCount = 0,
            totalAmount = 0.0,
            errorMessage = "Error de conexión: ${e.message}"
        )
    }

    private fun resetCartCounts() {
        _uiState.value = _uiState.value.copy(
            cartCount = 0,
            totalAmount = 0.0
        )
    }
}