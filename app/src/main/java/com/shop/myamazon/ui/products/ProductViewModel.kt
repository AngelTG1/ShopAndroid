package com.shop.myamazon.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.myamazon.data.remote.models.Product
import com.shop.myamazon.data.remote.repository.ProductRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val lastUpdated: Long = 0L,
    val isSearchMode: Boolean = false, // ✅ Nuevo: Para saber si estamos en modo búsqueda
    val currentSearchQuery: String = "" // ✅ Nuevo: Query actual de búsqueda
)

class ProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private var autoRefreshEnabled = true
    private val REFRESH_INTERVAL = 30_000L // 30 segundos

    init {
        loadProducts()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (autoRefreshEnabled) {
                delay(REFRESH_INTERVAL)
                if (autoRefreshEnabled && !_uiState.value.isLoading && !_uiState.value.isSearchMode) {
                    // ✅ Solo auto-refresh si NO estamos en modo búsqueda
                    loadProductsSilently()
                }
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSearchMode = false, // ✅ Salir del modo búsqueda
                currentSearchQuery = "" // ✅ Limpiar query
            )

            try {
                val response = productRepository.getAllProducts()

                if (response.isSuccessful) {
                    val productsResponse = response.body()

                    if (productsResponse?.status == "success") {
                        val products = productsResponse.data?.products ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            products = products,
                            lastUpdated = System.currentTimeMillis()
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = productsResponse?.message ?: "Error desconocido"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar productos: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    // ✅ Nueva función para buscar productos
    fun searchProducts(query: String) {
        if (query.isBlank()) {
            // Si la búsqueda está vacía, cargar todos los productos
            loadProducts()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSearchMode = true, // ✅ Activar modo búsqueda
                currentSearchQuery = query // ✅ Guardar query actual
            )

            try {
                val response = productRepository.searchProducts(searchQuery = query)

                if (response.isSuccessful) {
                    val productsResponse = response.body()
                    if (productsResponse?.status == "success") {
                        val products = productsResponse.data?.products ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            products = products,
                            errorMessage = null,
                            lastUpdated = System.currentTimeMillis()
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = productsResponse?.message ?: "Error en la búsqueda"
                        )
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "No se encontraron productos para \"$query\""
                        500 -> "Error del servidor"
                        else -> "Error de conexión (${response.code()})"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.localizedMessage ?: e.message}"
                )
            }
        }
    }

    // ✅ Función para limpiar la búsqueda y recargar todos los productos
    fun clearSearch() {
        loadProducts()
    }

    // ✅ Función para verificar si estamos en modo búsqueda
    fun isInSearchMode(): Boolean = _uiState.value.isSearchMode

    // ✅ Función para obtener el query actual
    fun getCurrentSearchQuery(): String = _uiState.value.currentSearchQuery

    private fun loadProductsSilently() {
        viewModelScope.launch {
            try {
                // ✅ Si estamos en modo búsqueda, usar la búsqueda actual
                val response = if (_uiState.value.isSearchMode && _uiState.value.currentSearchQuery.isNotBlank()) {
                    productRepository.searchProducts(_uiState.value.currentSearchQuery)
                } else {
                    productRepository.getAllProducts()
                }

                if (response.isSuccessful) {
                    val productsResponse = response.body()

                    if (productsResponse?.status == "success") {
                        val newProducts = productsResponse.data?.products ?: emptyList()
                        val currentProducts = _uiState.value.products

                        if (hasProductsChanged(currentProducts, newProducts)) {
                            _uiState.value = _uiState.value.copy(
                                products = newProducts,
                                lastUpdated = System.currentTimeMillis()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Silenciar errores de actualización automática
            }
        }
    }

    private fun hasProductsChanged(current: List<Product>, new: List<Product>): Boolean {
        if (current.size != new.size) return true

        return current.zip(new).any { (currentProduct, newProduct) ->
            currentProduct.id != newProduct.id ||
                    currentProduct.stockQuantity != newProduct.stockQuantity ||
                    currentProduct.price != newProduct.price ||
                    currentProduct.status != newProduct.status
        }
    }

    fun onProductAdded() {
        viewModelScope.launch {
            delay(1000)
            // ✅ Si estamos en búsqueda, mantener la búsqueda actual
            if (_uiState.value.isSearchMode && _uiState.value.currentSearchQuery.isNotBlank()) {
                searchProducts(_uiState.value.currentSearchQuery)
            } else {
                loadProductsSilently()
            }
        }
    }

    // ✅ Nueva función para refrescar manualmente (tanto búsqueda como productos normales)
    fun refresh() {
        if (_uiState.value.isSearchMode && _uiState.value.currentSearchQuery.isNotBlank()) {
            searchProducts(_uiState.value.currentSearchQuery)
        } else {
            loadProducts()
        }
    }

    fun pauseAutoRefresh() {
        autoRefreshEnabled = false
    }

    fun resumeAutoRefresh() {
        if (!autoRefreshEnabled) {
            autoRefreshEnabled = true
            startAutoRefresh()
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshEnabled = false
    }
}