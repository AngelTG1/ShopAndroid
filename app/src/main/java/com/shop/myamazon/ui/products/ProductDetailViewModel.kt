package com.shop.myamazon.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.myamazon.data.remote.models.Product
import com.shop.myamazon.data.remote.repository.ProductRepository
import com.shop.myamazon.data.remote.repository.CartRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.async

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val relatedProducts: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val viewsCount: Int = 0,
    val isAddingToCart: Boolean = false,
    val lastUpdated: Long = 0L,
    val successMessage: String? = null
)

class ProductDetailViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository() // ✅ Agregamos CartRepository

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // StateFlow para el ID del producto actual
    private val _currentProductId = MutableStateFlow<String?>(null)

    // StateFlow para controlar auto-refresh
    private val _autoRefreshEnabled = MutableStateFlow(true)

    private var refreshJob: Job? = null
    private var viewTrackingJob: Job? = null

    init {
        setupAutoRefresh()
    }

    // ✅ Configurar auto-refresh con Coroutines y StateFlow
    @OptIn(FlowPreview::class)
    private fun setupAutoRefresh() {
        viewModelScope.launch {
            // Combinar StateFlows para auto-refresh inteligente
            combine(
                _currentProductId,
                _autoRefreshEnabled.debounce(1000) // Evitar refresh excesivo
            ) { productId, autoRefreshEnabled ->
                productId to autoRefreshEnabled
            }.collect { (productId, autoRefreshEnabled) ->
                if (productId != null && autoRefreshEnabled) {
                    startPeriodicRefresh(productId)
                } else {
                    stopPeriodicRefresh()
                }
            }
        }
    }

    // ✅ Refresh periódico con Coroutines
    private suspend fun startPeriodicRefresh(productId: String) {
        refreshJob?.cancelAndJoin()
        refreshJob = viewModelScope.launch {
            while (_autoRefreshEnabled.value && _currentProductId.value == productId) {
                delay(30_000) // 30 segundos

                if (_autoRefreshEnabled.value && !_uiState.value.isLoading) {
                    refreshProductSilently(productId)
                }
            }
        }
    }

    private fun stopPeriodicRefresh() {
        refreshJob?.cancel()
        refreshJob = null
    }

    // ✅ Cargar producto principal con StateFlow - CORREGIDO
    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _currentProductId.value = productId

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                // ✅ CORREGIDO: Usar async correctamente dentro del viewModelScope
                val productDeferred = async {
                    productRepository.getProductByUuid(productId)
                }

                val productResponse = productDeferred.await()

                if (productResponse.isSuccessful) {
                    val product = productResponse.body()?.data

                    if (product != null) {
                        // ✅ CORREGIDO: Usar _uiState en lugar de *uiState
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            product = product,
                            viewsCount = product.viewsCount,
                            lastUpdated = System.currentTimeMillis()
                        )

                        // ✅ Cargar productos relacionados en background
                        loadRelatedProductsInBackground(product.category, product.id)

                        // ✅ Tracking de vistas en background
                        startViewTracking(productId)

                    } else {
                        handleError("Producto no encontrado")
                    }
                } else {
                    handleHttpError(productResponse.code())
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    // ✅ Cargar productos relacionados en background
    private fun loadRelatedProductsInBackground(category: String, excludeProductId: Int) {
        viewModelScope.launch {
            try {
                val response = productRepository.getAllProducts(
                    category = category,
                    limit = 5
                )

                if (response.isSuccessful) {
                    val products = response.body()?.data?.products
                        ?.filter { it.id != excludeProductId }
                        ?.take(4) // Solo 4 productos relacionados
                        ?: emptyList()

                    _uiState.value = _uiState.value.copy(
                        relatedProducts = products
                    )
                }
            } catch (e: Exception) {
                // Error silencioso para productos relacionados
            }
        }
    }

    // ✅ Tracking de vistas con Coroutines
    private fun startViewTracking(productId: String) {
        viewTrackingJob?.cancel()
        viewTrackingJob = viewModelScope.launch {
            delay(3000) // Esperar 3 segundos antes de contar vista

            if (_currentProductId.value == productId) {
                trackProductView(productId)
            }
        }
    }

    private suspend fun trackProductView(productId: String) {
        try {
            // TODO: Implementar endpoint para tracking de vistas
            // productRepository.trackView(productId)

            // Por ahora, incrementar localmente
            _uiState.value.product?.let { product ->
                val updatedProduct = product.copy(viewsCount = product.viewsCount + 1)
                _uiState.value = _uiState.value.copy(
                    product = updatedProduct,
                    viewsCount = updatedProduct.viewsCount
                )
            }
        } catch (e: Exception) {
            // Error silencioso para tracking
        }
    }

    // ✅ Refresh silencioso con StateFlow
    private suspend fun refreshProductSilently(productId: String) {
        try {
            val response = productRepository.getProductByUuid(productId)

            if (response.isSuccessful) {
                val newProduct = response.body()?.data

                if (newProduct != null) {
                    val currentProduct = _uiState.value.product

                    // Solo actualizar si hay cambios significativos
                    if (hasProductChanged(currentProduct, newProduct)) {
                        _uiState.value = _uiState.value.copy(
                            product = newProduct,
                            viewsCount = newProduct.viewsCount,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Error silencioso para refresh automático
        }
    }

    // ✅ Verificar cambios en el producto
    private fun hasProductChanged(current: Product?, new: Product): Boolean {
        if (current == null) return true

        return current.stockQuantity != new.stockQuantity ||
                current.price != new.price ||
                current.status != new.status ||
                current.viewsCount != new.viewsCount
    }

    // ✅ Agregar al carrito con StateFlow - INTEGRADO CON CARTREPOSITORY
    fun addToCart(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAddingToCart = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                // ✅ CORREGIDO: Usar tu CartRepository real
                val response = cartRepository.addToCart(productId, quantity)

                if (response.isSuccessful) {
                    val cartResponse = response.body()

                    if (cartResponse?.status == "success") {
                        _uiState.value = _uiState.value.copy(
                            isAddingToCart = false,
                            successMessage = "Producto agregado al carrito exitosamente"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isAddingToCart = false,
                            errorMessage = cartResponse?.message ?: "Error al agregar al carrito"
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
                        isAddingToCart = false,
                        errorMessage = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddingToCart = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    // ✅ Refresh manual
    fun refreshProduct() {
        _currentProductId.value?.let { productId ->
            _uiState.value = _uiState.value.copy(isRefreshing = true)

            viewModelScope.launch {
                refreshProductSilently(productId)
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    // ✅ Control de auto-refresh
    fun pauseAutoRefresh() {
        _autoRefreshEnabled.value = false
    }

    fun resumeAutoRefresh() {
        _autoRefreshEnabled.value = true
    }

    // ✅ Funciones helper para manejo de errores
    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    private fun handleHttpError(code: Int) {
        val errorMsg = when (code) {
            404 -> "Producto no encontrado"
            403 -> "No tienes permisos para ver este producto"
            500 -> "Error del servidor"
            else -> "Error al cargar producto: $code"
        }
        handleError(errorMsg)
    }

    private fun handleException(e: Exception) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = "Error de conexión: ${e.message}"
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        _autoRefreshEnabled.value = false
        refreshJob?.cancel()
        viewTrackingJob?.cancel()
    }
}