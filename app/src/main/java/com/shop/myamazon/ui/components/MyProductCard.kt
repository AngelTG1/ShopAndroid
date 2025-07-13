package com.shop.myamazon.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shop.myamazon.R
import com.shop.myamazon.data.remote.models.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProductCard(
    product: Product,
    onEdit: (Product) -> Unit = {},
    onDelete: (Product) -> Unit = {},
    isDeleting: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Estado del producto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProductStatusChip(status = product.status)

                Row {
                    // Botón editar
                    IconButton(
                        onClick = { onEdit(product) },
                        enabled = !isDeleting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color(0xFF4A5568)
                        )
                    }

                    // Botón eliminar
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFFE53E3E)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color(0xFFE53E3E)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Imagen del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del producto
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = product.getFormattedPrice(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.red_700)
                    )

                    Text(
                        text = "Stock: ${product.stockQuantity}",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Vistas: ${product.viewsCount}",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )

                    Text(
                        text = product.category,
                        fontSize = 12.sp,
                        color = Color(0xFF4A5568),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Eliminar Producto",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres eliminar \"${product.name}\"? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(product)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53E3E)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProductStatusChip(status: String) {
    val (color, text) = when (status.lowercase()) {
        "active" -> Color(0xFF48BB78) to "Activo"
        "inactive" -> Color(0xFF718096) to "Inactivo"
        "pending_approval" -> Color(0xFFED8936) to "Pendiente"
        "rejected" -> Color(0xFFE53E3E) to "Rechazado"
        else -> Color(0xFF718096) to status
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyProductCardPreview() {
    val sampleProduct = Product(
        id = 1,
        uuid = "sample-uuid",
        sellerId = 1,
        name = "Silla Ergonómica Premium",
        description = "Silla de oficina con soporte lumbar ajustable y materiales de alta calidad.",
        price = 250.00,
        stockQuantity = 8,
        category = "Furniture",
        images = listOf("https://example.com/chair.jpg"),
        status = "active",
        viewsCount = 125
    )

    MyProductCard(
        product = sampleProduct,
        onEdit = { println("Editar: ${it.name}") },
        onDelete = { println("Eliminar: ${it.name}") }
    )
}