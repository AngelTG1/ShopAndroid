package com.shop.shop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.shop.R
import com.shop.shop.ui.auth.AuthViewModel
import com.shop.shop.ui.components.CardPreview
import com.shop.shop.ui.components.UserHeader
import com.shop.shop.ui.products.ProductViewModel
import com.shop.shop.ui.cart.CartViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAddProduct: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val productUiState by productViewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val canAddProducts = authUiState.user?.role?.let { role ->
        role.equals("memberships", ignoreCase = true) ||
                role.equals("Admin", ignoreCase = true) ||
                role.equals("Premium", ignoreCase = true) ||
                role.equals("Vendedor", ignoreCase = true)
    } ?: false

    var addingProductIds by remember { mutableStateOf(setOf<Int>()) }

    // ✅ Obtener configuración de pantalla (alternativa más simple a BoxWithConstraints)
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // ✅ Valores adaptativos basados en el tamaño de pantalla
    val isCompactHeight = screenHeight < 600.dp
    val isCompactWidth = screenWidth < 400.dp

    val horizontalPadding = when {
        screenWidth < 400.dp -> 8.dp
        screenWidth < 600.dp -> 12.dp
        else -> 16.dp
    }

    val verticalPadding = when {
        screenHeight < 600.dp -> 4.dp
        screenHeight < 800.dp -> 8.dp
        else -> 12.dp
    }

    val titleTextSize = when {
        isCompactWidth -> 14.sp
        else -> 16.sp
    }

    val bodyTextSize = when {
        isCompactWidth -> 12.sp
        else -> 14.sp
    }

    val gridColumns = when {
        screenWidth < 400.dp -> 1
        screenWidth < 600.dp -> 1
        screenWidth < 900.dp -> 2
        else -> 3
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            productViewModel.loadProducts()
            isSearching = false
        } else {
            delay(500)
            isSearching = true
            productViewModel.searchProducts(searchQuery)
        }
    }

    LaunchedEffect(cartUiState.successMessage) {
        cartUiState.successMessage?.let {
            cartViewModel.clearMessages()
        }
    }

    LaunchedEffect(cartUiState.errorMessage) {
        cartUiState.errorMessage?.let {
            cartViewModel.clearMessages()
        }
    }

    LaunchedEffect(cartUiState.actionInProgress) {
        if (!cartUiState.actionInProgress) {
            addingProductIds = emptySet()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (canAddProducts) {
                FloatingActionButton(
                    onClick = onNavigateToAddProduct,
                    containerColor = colorResource(R.color.red_700),
                    contentColor = Color.White,
                    modifier = Modifier.size(
                        if (isCompactWidth) 48.dp else 56.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar producto",
                        modifier = Modifier.size(
                            if (isCompactWidth) 20.dp else 24.dp
                        )
                    )
                }
            }
        },
        containerColor = Color.White,
        // ✅ CLAVE: Eliminar padding del sistema para que llegue hasta arriba
        topBar = {
            // ✅ Header como TopBar para que llegue completamente arriba
            UserHeader(
                user = authUiState.user,
                cartCount = cartUiState.cartCount,
                onProfileClick = onNavigateToProfile,
                onCartClick = onNavigateToCart,
                onPlanesClick = onNavigateToSubscription,
                onLogoutClick = {
                    authViewModel.logout()
                    onLogout()
                },
                cartViewModel = cartViewModel,
                modifier = Modifier.padding(0.dp) // ✅ Cero padding
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.gray_500))
        ) {

            // ✅ Campo de búsqueda adaptativo
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .padding(vertical = verticalPadding),
                placeholder = {
                    Text(
                        text = "Buscar productos...",
                        color = Color(0xFF9CA3AF),
                        fontSize = bodyTextSize
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(
                            if (isCompactWidth) 18.dp else 20.dp
                        )
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                isSearching = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(
                                    if (isCompactWidth) 18.dp else 20.dp
                                )
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE53E3E),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFFE53E3E)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // ✅ Indicador de búsqueda adaptativo
            if (isSearching && searchQuery.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3F4F6)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = if (isCompactHeight) 8.dp else 12.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscando",
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(
                                if (isCompactWidth) 14.dp else 16.dp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resultados para: \"$searchQuery\"",
                            fontSize = bodyTextSize,
                            color = Color(0xFF6B7280)
                        )
                        if (productUiState.isLoading) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(
                                    if (isCompactWidth) 12.dp else 14.dp
                                ),
                                strokeWidth = 2.dp,
                                color = Color(0xFFE53E3E)
                            )
                        }
                    }
                }
            }

            // ✅ Contenido principal adaptativo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding)
            ) {
                when {
                    productUiState.isLoading && productUiState.products.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFE53E3E),
                                    modifier = Modifier.size(
                                        if (isCompactWidth) 32.dp else 40.dp
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isSearching) "Buscando productos..." else "Cargando productos...",
                                    color = Color(0xFF718096),
                                    fontSize = bodyTextSize
                                )
                            }
                        }
                    }

                    productUiState.errorMessage != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isSearching) "Error en la búsqueda" else "Error al cargar productos",
                                    color = Color(0xFFE53E3E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = titleTextSize
                                )
                                Text(
                                    text = productUiState.errorMessage!!,
                                    color = Color(0xFFE53E3E),
                                    fontSize = bodyTextSize
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (isSearching && searchQuery.isNotEmpty()) {
                                            productViewModel.searchProducts(searchQuery)
                                        } else {
                                            productViewModel.loadProducts()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53E3E)
                                    )
                                ) {
                                    Text(
                                        "Reintentar",
                                        fontSize = bodyTextSize
                                    )
                                }
                            }
                        }
                    }

                    productUiState.products.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isSearching && searchQuery.isNotEmpty()) {
                                    Text(
                                        text = "No se encontraron productos para \"$searchQuery\"",
                                        color = Color(0xFF718096),
                                        fontSize = titleTextSize
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Intenta con otros términos de búsqueda",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = bodyTextSize
                                    )
                                } else {
                                    Text(
                                        text = "No hay productos disponibles",
                                        color = Color(0xFF718096),
                                        fontSize = titleTextSize
                                    )

                                    if (canAddProducts) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "¡Sé el primero en agregar un producto!",
                                            color = Color(0xFFE53E3E),
                                            fontSize = bodyTextSize
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // ✅ Grid de productos completamente adaptativo
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridColumns),
                            contentPadding = PaddingValues(
                                bottom = if (canAddProducts) 25.dp else 5.dp,
                                top = 28.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                if (isCompactWidth) 6.dp else 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                if (isCompactHeight) 6.dp else 8.dp
                            )
                        ) {
                            items(productUiState.products) { product ->
                                val cartItem = cartUiState.cart?.items?.find { it.productId == product.id }
                                val isInCart = cartItem != null
                                val cartQuantity = cartItem?.quantity ?: 0

                                CardPreview(
                                    product = product,
                                    isAddingToCart = addingProductIds.contains(product.id),
                                    isInCart = isInCart,
                                    cartQuantity = cartQuantity,
                                    onAddToCart = { selectedProduct ->
                                        if (!isInCart) {
                                            addingProductIds = addingProductIds + selectedProduct.id
                                            cartViewModel.addToCart(selectedProduct.id, 1)
                                        }
                                    },
                                    onViewCart = {
                                        onNavigateToCart()
                                    },
                                    onViewDetail = { selectedProduct ->
                                        onNavigateToProductDetail(selectedProduct.uuid)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}