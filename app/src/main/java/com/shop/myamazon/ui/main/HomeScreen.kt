package com.shop.myamazon.ui.main

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.myamazon.R
import com.shop.myamazon.ui.auth.AuthViewModel
import com.shop.myamazon.ui.components.CardPreview
import com.shop.myamazon.ui.components.UserHeader
import com.shop.myamazon.ui.products.ProductViewModel
import com.shop.myamazon.ui.cart.CartViewModel
import com.shop.myamazon.data.remote.models.Product
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAddProduct: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val productUiState by productViewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()

    // ✅ Estado para el input de búsqueda
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // ✅ Verificar si el usuario puede agregar productos según el backend
    val canAddProducts = authUiState.user?.role?.let { role ->
        role.equals("memberships", ignoreCase = true) ||
                role.equals("Admin", ignoreCase = true) ||
                role.equals("Premium", ignoreCase = true) ||
                role.equals("Vendedor", ignoreCase = true)
    } ?: false

    // ✅ Estado para tracking de productos siendo agregados
    var addingProductIds by remember { mutableStateOf(setOf<Int>()) }

    // ✅ Función para realizar búsqueda con debounce
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            // Si no hay búsqueda, cargar todos los productos
            productViewModel.loadProducts()
            isSearching = false
        } else {
            // Esperar 500ms antes de buscar (debounce)
            delay(500)
            isSearching = true
            productViewModel.searchProducts(searchQuery)
        }
    }

    // ✅ Mostrar mensajes del carrito
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

    // ✅ Limpiar estado de carga cuando termina la acción
    LaunchedEffect(cartUiState.actionInProgress) {
        if (!cartUiState.actionInProgress) {
            addingProductIds = emptySet()
        }
    }

    Scaffold(
        // ✅ Botón flotante que solo aparece para usuarios con permisos
        floatingActionButton = {
            if (canAddProducts) {
                FloatingActionButton(
                    onClick = onNavigateToAddProduct,
                    containerColor = colorResource(R.color.red_700),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar producto"
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // ✅ Header con menú dropdown y badge del carrito
            UserHeader(
                user = authUiState.user,
                cartCount = cartUiState.cartCount,
                onProfileClick = onNavigateToProfile,
                onCartClick = onNavigateToCart,
                onLogoutClick = {
                    authViewModel.logout()
                    onLogout()
                },
                cartViewModel = cartViewModel
            )

            // ✅ Campo de búsqueda

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = {
                        Text(
                            text = "Buscar productos...",
                            color = Color(0xFF9CA3AF)
                        )

                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF6B7280)
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
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE53E3E), // Usando color directo para preview
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFFE53E3E)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )


            // ✅ Indicador de búsqueda activa
            if (isSearching && searchQuery.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3F4F6)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscando",
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resultados para: \"$searchQuery\"",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        if (productUiState.isLoading) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFFE53E3E)
                            )
                        }
                    }
                }
            }

            // ✅ Contenido principal con productos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 9.dp)
            ) {
                // Estado de carga
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
                                    color = Color(0xFFE53E3E)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (isSearching) "Buscando productos..." else "Cargando productos...",
                                    color = Color(0xFF718096)
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
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = productUiState.errorMessage!!,
                                    color = Color(0xFFE53E3E)
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
                                    Text("Reintentar")
                                }
                            }
                        }
                    }

                    productUiState.products.isEmpty() -> {
                        Column {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (isSearching && searchQuery.isNotEmpty()) {
                                        Text(
                                            text = "No se encontraron productos para \"$searchQuery\"",
                                            color = Color(0xFF718096),
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Intenta con otros términos de búsqueda",
                                            color = Color(0xFF9CA3AF),
                                            fontSize = 14.sp
                                        )
                                    } else {
                                        Text(
                                            text = "No hay productos disponibles",
                                            color = Color(0xFF718096),
                                            fontSize = 16.sp
                                        )

                                        if (canAddProducts) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "¡Sé el primero en agregar un producto!",
                                                color = Color(0xFFE53E3E),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Grid de productos
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(bottom = 70.dp, top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productUiState.products) { product ->
                                // ✅ Verificar si el producto está en el carrito
                                val cartItem = cartUiState.cart?.items?.find { it.productId == product.id }
                                val isInCart = cartItem != null
                                val cartQuantity = cartItem?.quantity ?: 0

                                // En la sección donde llamas a CardPreview, cambia esta línea:

                                CardPreview(
                                    product = product,
                                    isAddingToCart = addingProductIds.contains(product.id), // ✅ Solo este producto específico
                                    isInCart = isInCart,
                                    cartQuantity = cartQuantity,
                                    onAddToCart = { selectedProduct ->
                                        // ✅ Solo agregar si no está en el carrito
                                        if (!isInCart) {
                                            addingProductIds = addingProductIds + selectedProduct.id
                                            cartViewModel.addToCart(selectedProduct.id, 1)
                                        }
                                    },
                                    onViewCart = {
                                        onNavigateToCart()
                                    },
                                    // ✅ Nueva función para ir al detalle del producto
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

// ✅ PREVIEWS PARA DISEÑO

@Preview(name = "HomeScreen - Loading", showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    MaterialTheme {
        HomeScreenPreview(isLoading = true)
    }
}

@Preview(name = "HomeScreen - With Products", showBackground = true)
@Composable
fun HomeScreenWithProductsPreview() {
    MaterialTheme {
        HomeScreenPreview(isLoading = false, hasProducts = true)
    }
}

@Preview(name = "HomeScreen - Empty", showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    MaterialTheme {
        HomeScreenPreview(isLoading = false, hasProducts = false)
    }
}

@Preview(name = "HomeScreen - Error", showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    MaterialTheme {
        HomeScreenPreview(isLoading = false, hasError = true)
    }
}

@Preview(name = "HomeScreen - Search Mode", showBackground = true)
@Composable
fun HomeScreenSearchPreview() {
    MaterialTheme {
        HomeScreenPreview(isLoading = false, hasProducts = true, isSearching = true, searchQuery = "iPhone")
    }
}

// ✅ Composable auxiliar para los previews
@Composable
private fun HomeScreenPreview(
    isLoading: Boolean = false,
    hasProducts: Boolean = false,
    hasError: Boolean = false,
    isSearching: Boolean = false,
    searchQuery: String = ""
) {
    var searchText by remember { mutableStateOf(searchQuery) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFFE53E3E),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto"
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Header simulado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Mi Amazons",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53E3E)
                )
            }

            // Campo de búsqueda

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = {
                        Text(
                            text = "Buscar productos...",
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF6B7280)
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = Color(0xFF6B7280)
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


            // Indicador de búsqueda
            if (isSearching && searchText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscando",
                            tint = Color(0xFFE53E3E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resultados para: \"$searchText\"",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFFE53E3E))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando productos...",
                                    color = Color(0xFF718096)
                                )
                            }
                        }
                    }

                    hasError -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error al cargar productos",
                                    color = Color(0xFFE53E3E),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Error de conexión",
                                    color = Color(0xFFE53E3E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53E3E)
                                    )
                                ) {
                                    Text("Reintentar")
                                }
                                Spacer(modifier = Modifier.height(19.dp))
                            }
                        }
                    }

                    !hasProducts -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isSearching) "No se encontraron productos para \"$searchText\""
                                    else "No hay productos disponibles",
                                    color = Color(0xFF718096),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }



                    else -> {
                        // Lista de productos de muestra
                        LazyVerticalGrid(

                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(bottom = 80.dp, top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),

                        ) {

                            items(getSampleProducts()) { product ->
                                CardPreview(
                                    product = product,
                                    onAddToCart = { },
                                    onViewCart = { },
                                    onViewDetail = { }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ Productos de muestra para el preview
private fun getSampleProducts() = listOf(
    Product(
        id = 1,
        uuid = "uuid-1",
        sellerId = 1,
        name = "iPhone 15 Pro Max",
        description = "El iPhone más avanzado con cámara de 48MP y chip A17 Pro. Diseño premium en titanio con pantalla Super Retina XDR.",
        price = 1299.99,
        stockQuantity = 15,
        category = "Electrónicos",
        images = listOf("https://ik.imagekit.io/ziosam2gq/iphone686_FZ.jpg?updatedAt=1752361004586.jpg"),
        status = "active",
        viewsCount = 234
    ),
    Product(
        id = 2,
        uuid = "uuid-2",
        sellerId = 2,
        name = "MacBook Air M3",
        description = "Laptop ultraligera con chip M3, perfecta para trabajo y estudio. Hasta 18 horas de batería.",
        price = 999.00,
        stockQuantity = 3,
        category = "Computadoras",
        images = listOf("https://example.com/macbook.jpg"),
        status = "active",
        viewsCount = 156
    ),
    Product(
        id = 3,
        uuid = "uuid-3",
        sellerId = 1,
        name = "AirPods Pro (3ra Gen)",
        description = "Auriculares inalámbricos con cancelación activa de ruido y audio espacial personalizado.",
        price = 249.99,
        stockQuantity = 0,
        category = "Audio",
        images = listOf("https://example.com/airpods.jpg"),
        status = "active",
        viewsCount = 89
    )
)