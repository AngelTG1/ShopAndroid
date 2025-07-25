package com.shop.shop.ui.products

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.shop.data.remote.models.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProductsScreen(
    onBack: () -> Unit,
    onEditProduct: (Product) -> Unit = {},
    onAddProduct: () -> Unit = {},
    onViewProduct: (Product) -> Unit = {}, // ✅ CORREGIDO: Parámetro agregado
    myProductsViewModel: MyProductsViewModel = viewModel()
) {
    val uiState by myProductsViewModel.uiState.collectAsState()

    // Mostrar mensaje de éxito al eliminar
    LaunchedEffect(uiState.deleteSuccess) {
        uiState.deleteSuccess?.let {
            myProductsViewModel.clearDeleteSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
    ) {
        // Header
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mis Productos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )

                    Text(
                        text = "${uiState.myProducts.size} productos publicados",
                        fontSize = 14.sp,
                        color = Color(0xFF718096)
                    )
                }

                // Botón de refrescar
                IconButton(
                    onClick = { myProductsViewModel.refreshMyProducts() },
                    enabled = !uiState.isRefreshing
                ) {
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFFE53E3E)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = Color(0xFF4A5568)
                        )
                    }
                }

                // Botón de agregar producto
                IconButton(onClick = onAddProduct) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar producto",
                        tint = Color(0xFFE53E3E)
                    )
                }
            }
        }

        // Contenido principal
        when {
            uiState.isLoading && uiState.myProducts.isEmpty() -> {
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
                            text = "Cargando tus productos...",
                            color = Color(0xFF718096)
                        )
                    }
                }
            }

            uiState.errorMessage != null -> {
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
                            text = "Error al cargar productos",
                            color = Color(0xFFE53E3E),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.errorMessage!!,
                            color = Color(0xFFE53E3E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { myProductsViewModel.loadMyProducts() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53E3E)
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            uiState.myProducts.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Sin productos",
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF718096)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No tienes productos publicados",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¡Comienza a vender agregando tu primer producto!",
                        fontSize = 14.sp,
                        color = Color(0xFF718096)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onAddProduct,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53E3E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Producto")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.myProducts) { product ->
                        MyProductCardWithActions(
                            product = product,
                            onEdit = { onEditProduct(it) },
                            onDelete = { productToDelete ->
                                myProductsViewModel.deleteProduct(productToDelete.uuid)
                            },
                            onViewDetail = { onViewProduct(it) }, // ✅ AGREGADO: Ver detalle
                            isDeleting = uiState.isDeleting
                        )
                    }
                }
            }
        }
    }
}

// ✅ NUEVO: Componente MyProductCard con acciones extendidas
@Composable
private fun MyProductCardWithActions(
    product: Product,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onViewDetail: (Product) -> Unit,
    isDeleting: Boolean
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
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
            // Header con estado y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProductStatusChip(status = product.status)

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // ✅ AGREGADO: Botón Ver Detalle
                    IconButton(
                        onClick = { onViewDetail(product) },
                        enabled = !isDeleting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Ver detalle",
                            tint = Color(0xFF059669)
                        )
                    }

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

            Spacer(modifier = Modifier.height(12.dp))

            // Resto del contenido del producto
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color(0xFF718096),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.getFormattedPrice(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53E3E)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Stock: ${product.stockQuantity}",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )
                    Text(
                        text = "${product.viewsCount} vistas",
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
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