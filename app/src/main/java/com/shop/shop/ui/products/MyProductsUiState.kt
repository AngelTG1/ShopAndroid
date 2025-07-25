package com.shop.shop.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shop.data.remote.models.Product
import com.shop.shop.data.remote.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyProductsUiState(
    val isLoading: Boolean = false,
    val myProducts: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteSuccess: String? = null
)

class MyProductsViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow(MyProductsUiState())
    val uiState: StateFlow<MyProductsUiState> = _uiState.asStateFlow()

    init {
        loadMyProducts()
    }

    fun loadMyProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                deleteSuccess = null
            )

            try {
                val response = productRepository.getMyProducts()

                if (response.isSuccessful) {
                    val productsResponse = response.body()

                    if (productsResponse?.status == "success") {
                        val products = productsResponse.data?.products ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            myProducts = products
                        )

                        println("✅ Mis productos cargados: ${products.size}")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = productsResponse?.message ?: "Error desconocido"
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No tienes permisos para ver tus productos"
                        403 -> "Necesitas una membresía Premium para gestionar productos"
                        else -> "Error al cargar productos: ${response.code()}"
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

                println("❌ Error cargando mis productos: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun refreshMyProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                errorMessage = null,
                deleteSuccess = null
            )

            try {
                val response = productRepository.getMyProducts()

                if (response.isSuccessful) {
                    val productsResponse = response.body()

                    if (productsResponse?.status == "success") {
                        val products = productsResponse.data?.products ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isRefreshing = false,
                            myProducts = products
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isRefreshing = false,
                            errorMessage = productsResponse?.message ?: "Error desconocido"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = "Error al actualizar productos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun deleteProduct(productUuid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                errorMessage = null,
                deleteSuccess = null
            )

            try {
                val response = productRepository.deleteProduct(productUuid)

                if (response.isSuccessful) {
                    val deleteResponse = response.body()

                    if (deleteResponse?.status == "success") {
                        // Remover el producto de la lista local
                        val updatedProducts = _uiState.value.myProducts.filter {
                            it.uuid != productUuid
                        }

                        _uiState.value = _uiState.value.copy(
                            isDeleting = false,
                            myProducts = updatedProducts,
                            deleteSuccess = "Producto eliminado exitosamente"
                        )

                        println("✅ Producto eliminado: $productUuid")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isDeleting = false,
                            errorMessage = deleteResponse?.message ?: "Error al eliminar producto"
                        )
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "No tienes permisos para eliminar este producto"
                        403 -> "No puedes eliminar este producto"
                        404 -> "Producto no encontrado"
                        else -> "Error al eliminar producto: ${response.code()}"
                    }

                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = "Error: ${e.message}"
                )

                println("❌ Error eliminando producto: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearDeleteSuccess() {
        _uiState.value = _uiState.value.copy(deleteSuccess = null)
    }
}