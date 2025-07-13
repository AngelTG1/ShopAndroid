package com.shop.myamazon.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shop.myamazon.R
import com.shop.myamazon.data.remote.models.Product
import com.shop.myamazon.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    viewModel: ProductDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ Cargar producto cuando cambie el ID
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // ✅ Pausar auto-refresh cuando la pantalla no está visible
    DisposableEffect(Unit) {
        viewModel.resumeAutoRefresh()
        onDispose {
            viewModel.pauseAutoRefresh()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
    ) {
        // ✅ Header con botón back
        ProductDetailHeader(
            onBack = onBack,
            onRefresh = viewModel::refreshProduct,
            isRefreshing = uiState.isRefreshing
        )

        when {
            uiState.isLoading && uiState.product == null -> {
                ProductDetailLoadingState()
            }

            uiState.errorMessage != null -> {
                ProductDetailErrorState(
                    errorMessage = uiState.errorMessage!!,
                    onRetry = { viewModel.loadProduct(productId) },
                    onClearError = viewModel::clearError
                )
            }

            uiState.product != null -> {
                ProductDetailContent(
                    product = uiState.product!!,
                    relatedProducts = uiState.relatedProducts,
                    viewsCount = uiState.viewsCount,
                    isAddingToCart = uiState.isAddingToCart,
                    onAddToCart = viewModel::addToCart,
                    onNavigateToCart = onNavigateToCart,
                    onNavigateToProduct = onNavigateToProduct,
                    lastUpdated = uiState.lastUpdated
                )
            }
        }
    }
}

@Composable
private fun ProductDetailHeader(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF4A5568)
                    )
                }

                Text(
                    text = "Detalle del Producto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.red_700)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        tint = Color(0xFF4A5568)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.red_700),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando producto...",
                color = Color(0xFF718096),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ProductDetailErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color(0xFFE53E3E),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Error al cargar producto",
                color = Color(0xFFE53E3E),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = errorMessage,
                color = Color(0xFFE53E3E),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearError
                ) {
                    Text("Cerrar")
                }

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.red_700)
                    )
                ) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    relatedProducts: List<Product>,
    viewsCount: Int,
    isAddingToCart: Boolean,
    onAddToCart: (Int, Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    lastUpdated: Long
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProductMainInfo(
                product = product,
                viewsCount = viewsCount,
                lastUpdated = lastUpdated
            )
        }

        item {
            ProductActions(
                product = product,
                isAddingToCart = isAddingToCart,
                onAddToCart = onAddToCart,
                onNavigateToCart = onNavigateToCart
            )
        }

        item {
            ProductDescription(product = product)
        }

        if (relatedProducts.isNotEmpty()) {
            item {
                ProductRelatedSection(
                    relatedProducts = relatedProducts,
                    onNavigateToProduct = onNavigateToProduct
                )
            }
        }
    }
}

@Composable
private fun ProductMainInfo(
    product: Product,
    viewsCount: Int,
    lastUpdated: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen principal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7FAFC)),
                contentAlignment = Alignment.Center
            ) {
                if (product.getFirstImage() != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.getFirstImage())
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Sin imagen",
                        color = Color(0xFF718096),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre y precio
            Text(
                text = product.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = product.getFormattedPrice(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.red_700)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Stock: ${product.stockQuantity}",
                        fontSize = 14.sp,
                        color = Color(0xFF718096)
                    )
                    Text(
                        text = "$viewsCount vistas",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductActions(
    product: Product,
    isAddingToCart: Boolean,
    onAddToCart: (Int, Int) -> Unit,
    onNavigateToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onAddToCart(product.id, 1) },
                modifier = Modifier.weight(1f),
                enabled = product.isAvailable() && !isAddingToCart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.red_700)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isAddingToCart) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isAddingToCart) "Agregando..." else "Agregar al carrito",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = onNavigateToCart,
                modifier = Modifier.weight(0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Ver carrito",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ProductDescription(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Descripción",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.description,
                fontSize = 16.sp,
                color = Color(0xFF4A5568),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFE2E8F0))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Categoría",
                        fontSize = 14.sp,
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = product.category,
                        fontSize = 16.sp,
                        color = Color(0xFF2D3748),
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Estado",
                        fontSize = 14.sp,
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (product.isAvailable()) "Disponible" else "No disponible",
                        fontSize = 16.sp,
                        color = if (product.isAvailable()) Color(0xFF48BB78) else Color(0xFFE53E3E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductRelatedSection(
    relatedProducts: List<Product>,
    onNavigateToProduct: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Productos Relacionados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(relatedProducts) { product ->
                    ProductCard(
                        product = product,
                        modifier = Modifier.width(200.dp),
                        onAddToCart = { /* TODO */ },
                        onViewCart = { /* TODO */ }
                    )
                }
            }
        }
    }
}