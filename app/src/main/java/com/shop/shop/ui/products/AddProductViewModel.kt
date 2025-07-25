package com.shop.shop.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shop.data.remote.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddProductUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    fun addProduct(
        name: String,
        description: String,
        price: Double,
        stockQuantity: Int,
        category: String,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSuccess = false
            )

            try {
                val images = imageUrl?.takeIf { it.isNotBlank() }?.let { listOf(it) } ?: emptyList()

                val response = productRepository.createProduct(
                    name = name,
                    description = description,
                    price = price,
                    stockQuantity = stockQuantity,
                    category = category,
                    images = images
                )

                if (response.isSuccessful) {
                    val productResponse = response.body()

                    if (productResponse?.status == "success") {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = productResponse?.message ?: "Error desconocido"
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No tienes permisos para agregar productos"
                        403 -> "Necesitas una membresía Premium para agregar productos"
                        400 -> "Datos del producto inválidos"
                        500 -> "Error del servidor, intenta más tarde"
                        else -> "Error al crear producto: ${response.code()}"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}