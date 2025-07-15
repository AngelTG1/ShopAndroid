package com.shop.myamazon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shop.myamazon.R
import com.shop.myamazon.data.remote.models.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit = {},
    onViewCart: () -> Unit = {},
    onViewDetail: (Product) -> Unit = {}, // ✅ AGREGADO: Nueva función para ver detalle
    isAddingToCart: Boolean = false,
    isInCart: Boolean = false,
    cartQuantity: Int = 0,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onViewDetail(product) }, // ✅ AGREGADO: Click en toda la card para ir al detalle
            colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen del producto
            ProductImage(
                imageUrl = product.getFirstImage(),
                productName = product.name,
                isInCart = isInCart,
                cartQuantity = cartQuantity,
                onViewDetail = { onViewDetail(product) } // ✅ Click en imagen también navega
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Información del producto
            ProductInfo(product = product)

            Spacer(modifier = Modifier.height(12.dp))

            // Precio y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = product.getFormattedPrice(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.red_700)
                    )

                    // ✅ AGREGADO: Botón "Ver detalle" pequeño
                    TextButton(
                        onClick = { onViewDetail(product) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Ver detalle",
                            fontSize = 12.sp,
                            color = colorResource(R.color.red_700)
                        )
                    }
                }

                // Botones de acción
                ProductActionButtons(
                    product = product,
                    isAddingToCart = isAddingToCart,
                    isInCart = isInCart,
                    onAddToCart = onAddToCart,
                    onViewCart = onViewCart
                )
            }

            // Información adicional
            ProductAdditionalInfo(
                product = product,
                isInCart = isInCart,
                cartQuantity = cartQuantity
            )
        }
    }
}

@Composable
private fun ProductImage(
    imageUrl: String?,
    productName: String,
    isInCart: Boolean,
    cartQuantity: Int,
    onViewDetail: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7FAFC))
            .clickable { onViewDetail() }, // ✅ Click en imagen navega al detalle
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = productName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "Sin imagen",
                color = Color(0xFF718096),
                fontSize = 14.sp
            )
        }

        if (isInCart) {
            CartBadge(quantity = cartQuantity)
        }

        // ✅ AGREGADO: Botón flotante "Ver más" en la esquina inferior derecha
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            FloatingActionButton(
                onClick = onViewDetail,
                modifier = Modifier.size(40.dp),
                containerColor = Color.White.copy(alpha = 0.9f),
                contentColor = colorResource(R.color.red_700)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Ver detalle",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CartBadge(quantity: Int) {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .background(
                color = Color(0xFF10B981),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "En carrito",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = quantity.toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProductInfo(product: Product) {
    Text(
        text = product.name,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2D3748),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = product.description,
        fontSize = 14.sp,
        color = Color(0xFF718096),
        maxLines = 2, // ✅ Reducido para dar espacio al botón "Ver detalle"
        overflow = TextOverflow.Ellipsis,
        lineHeight = 20.sp
    )
}

@Composable
private fun ProductActionButtons(
    product: Product,
    isAddingToCart: Boolean,
    isInCart: Boolean,
    onAddToCart: (Product) -> Unit,
    onViewCart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when {
            !product.isAvailable() -> {
                DisabledButton(text = "Agotado")
            }
            isAddingToCart -> {
                LoadingButton()
            }
            isInCart -> {
                ViewCartButton(onClick = onViewCart)
            }
            else -> {
                AddToCartButton(
                    product = product,
                    onClick = { onAddToCart(product) }
                )
            }
        }
    }
}

@Composable
private fun DisabledButton(text: String) {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9CA3AF),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = false,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LoadingButton() {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9CA3AF),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = false,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 1.5.dp,
                color = Color.White
            )
            Text(
                text = "...",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ViewCartButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6B7280),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Ver carrito",
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Ver",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AddToCartButton(
    product: Product,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.red_700),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Agregar al carrito",
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (product.stockQuantity <= 5) "¡Últimas!" else "Agregar",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProductAdditionalInfo(
    product: Product,
    isInCart: Boolean,
    cartQuantity: Int
) {
    if (product.stockQuantity <= 5 && product.stockQuantity > 0 && !isInCart) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¡Solo quedan ${product.stockQuantity}!",
            fontSize = 12.sp,
            color = Color(0xFFE53E3E),
            fontWeight = FontWeight.Medium
        )
    }

    if (isInCart && cartQuantity > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "En tu carrito:",
                fontSize = 12.sp,
                color = Color(0xFF166534),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$cartQuantity ${if (cartQuantity == 1) "unidad" else "unidades"}",
                fontSize = 12.sp,
                color = Color(0xFF166534),
                fontWeight = FontWeight.Bold
            )
        }
    }
}