package com.shop.shop.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shop.shop.R
import com.shop.shop.data.remote.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout: () -> Unit = {},
    cartViewModel: CartViewModel = viewModel()
) {
    val cartUiState by cartViewModel.uiState.collectAsState()

    // ✅ Cargar carrito al iniciar
    LaunchedEffect(Unit) {
        cartViewModel.loadCart()
    }

    // ✅ Mostrar mensajes
    LaunchedEffect(cartUiState.successMessage) {
        cartUiState.successMessage?.let {
            println("✅ $it")
            cartViewModel.clearMessages()
        }
    }

    LaunchedEffect(cartUiState.errorMessage) {
        cartUiState.errorMessage?.let {
            println("❌ $it")
            cartViewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
    ) {
        // ✅ Header
        CartHeader(
            onBack = onBack,
            onClearCart = { cartViewModel.clearCart() },
            canClearCart = !cartUiState.cart?.isEmpty()!!,
            isLoading = cartUiState.actionInProgress
        )

        // ✅ Contenido del carrito
        when {
            cartUiState.isLoading -> {
                CartLoadingState()
            }

            cartUiState.errorMessage != null -> {
                CartErrorState(
                    errorMessage = cartUiState.errorMessage!!,
                    onRetry = { cartViewModel.loadCart() }
                )
            }

            cartUiState.cart?.isEmpty() != false -> {
                CartEmptyState()
            }

            else -> {
                // ✅ Carrito con productos (solo se ejecuta si cart no es null y no está vacío)
                cartUiState.cart?.let { cart ->
                    CartWithItemsContent(
                        cart = cart,
                        onUpdateQuantity = { itemId, quantity ->
                            cartViewModel.updateItemQuantity(itemId, quantity)
                        },
                        onRemoveItem = { itemId ->
                            cartViewModel.removeItem(itemId)
                        },
                        onCheckout = onCheckout,
                        isUpdating = cartUiState.actionInProgress
                    )
                }
            }
        }
    }
}

@Composable
private fun CartHeader(
    onBack: () -> Unit,
    onClearCart: () -> Unit,
    canClearCart: Boolean,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF4A5568)
                )
            }

            Text(
                text = "Mi Carrito",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )

            // Botón para vaciar carrito
            if (canClearCart) {
                IconButton(
                    onClick = onClearCart,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Vaciar carrito",
                        tint = Color(0xFFE53E3E)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.red_700)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando carrito...",
                color = Color(0xFF718096)
            )
        }
    }
}

@Composable
private fun CartErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error al cargar carrito",
                color = Color(0xFFE53E3E),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = errorMessage,
                color = Color(0xFFE53E3E),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53E3E)
                )
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun CartEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Carrito vacío",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF718096)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tu carrito está vacío",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¡Agrega productos para comenzar a comprar!",
                color = Color(0xFF718096),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CartWithItemsContent(
    cart: Cart,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    isUpdating: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Lista de productos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cart.items) { item ->
                CartItemCard(
                    item = item,
                    onUpdateQuantity = { newQuantity ->
                        onUpdateQuantity(item.uuid, newQuantity)
                    },
                    onRemoveItem = {
                        onRemoveItem(item.uuid)
                    },
                    isUpdating = isUpdating
                )
            }
        }

        // ✅ Resumen del carrito
        CartSummaryCard(
            cart = cart,
            onCheckout = onCheckout,
            isLoading = isUpdating
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemCard(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    isUpdating: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7FAFC)),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = item.product?.getFirstImage()
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Sin imagen",
                        tint = Color(0xFF718096)
                    )
                }
            }

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.product?.name ?: "Producto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.getFormattedUnitPrice(),
                    fontSize = 14.sp,
                    color = colorResource(R.color.red_700),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón disminuir
                    IconButton(
                        onClick = {
                            if (item.quantity > 1) {
                                onUpdateQuantity(item.quantity - 1)
                            }
                        },
                        enabled = !isUpdating && item.quantity > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = Color(0xFF4A5568)
                        )
                    }

                    // Cantidad
                    Text(
                        text = item.quantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Botón aumentar
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        enabled = !isUpdating,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = Color(0xFF4A5568)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón eliminar
                    IconButton(
                        onClick = onRemoveItem,
                        enabled = !isUpdating
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color(0xFFE53E3E)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartSummaryCard(
    cart: Cart,
    onCheckout: () -> Unit,
    isLoading: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Resumen del Pedido",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Productos (${cart.totalItems})",
                    color = Color(0xFF718096)
                )
                Text(
                    text = cart.getFormattedTotal(),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Envío",
                    color = Color(0xFF718096)
                )
                Text(
                    text = "GRATIS",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF10B981)
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color(0xFFE2E8F0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Text(
                    text = cart.getFormattedTotal(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.red_700)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.red_700),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Proceder al Pago",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    CartScreen(
        onBack = { },
        onCheckout = { }
    )
}