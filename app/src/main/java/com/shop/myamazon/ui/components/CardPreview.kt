package com.shop.myamazon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shop.myamazon.R
import com.shop.myamazon.data.remote.models.Product

@Composable
fun CardPreview(
    product: Product,
    onAddToCart: (Product) -> Unit = {},
    onViewCart: () -> Unit = {},
    onViewDetail: (Product) -> Unit = {},
    isAddingToCart: Boolean = false,
    isInCart: Boolean = false,
    cartQuantity: Int = 0,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(210.dp)
            .fillMaxWidth()
            .clickable { onViewDetail(product) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(180.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(R.color.gray_500))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.getFirstImage())
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Columna derecha con contenido
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Parte superior: título, autor y descripción
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "by ${product.category}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Parte inferior: precio y botón
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.getFormattedPrice(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Button(
                        onClick = {
                            when {
                                isInCart -> onViewCart()
                                else -> onAddToCart(product)
                            }
                        },
                        shape = RoundedCornerShape(19.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isInCart -> Color(0xFF6B7280) // Gris cuando está en carrito
                                else -> colorResource(R.color.red_700) // Rojo original para "Buy"
                            },
                            contentColor = Color.White // Texto/icono blanco para ambos casos
                        ),
                        contentPadding = PaddingValues(horizontal = 1.dp, vertical = 3.dp),
                        enabled = !isAddingToCart && product.stockQuantity > 0
                    ) {
                        when {
                            isAddingToCart -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            }
                            isInCart -> {
                                Icon(
                                    painter = painterResource(R.drawable.solar_cart),
                                    contentDescription = "Ver",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            product.stockQuantity <= 0 -> {
                                Text(text = "Agotado")
                            }
                            else -> {
                                Text(text = "Buy")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreviewPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(9.dp)
    ) {
        CardPreview(
            product = Product(
                id = 1,
                uuid = "uuid-1",
                sellerId = 1,
                name = "Malik Chair",
                description = "Ergonomical for human body curve.",
                price = 221.00,
                stockQuantity = 15,
                category = "Muebles",
                images = listOf(),
                status = "active",
                viewsCount = 45
            )
        )
    }
}