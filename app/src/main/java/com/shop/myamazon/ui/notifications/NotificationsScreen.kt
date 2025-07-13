package com.shop.myamazon.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shop.myamazon.R
import com.shop.myamazon.ui.auth.AuthViewModel

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false
)

enum class NotificationType {
    ORDER, PROMOTION, SYSTEM, CART
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    authViewModel: AuthViewModel
) {
    val authUiState by authViewModel.uiState.collectAsState()

    val notifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                title = "¡Producto agregado!",
                message = "Has agregado 'Silla Ergonómica Premium' a tu carrito",
                type = NotificationType.CART,
                timestamp = "Hace 5 min"
            ),
            NotificationItem(
                id = "2",
                title = "Oferta especial",
                message = "¡50% de descuento en productos de oficina! Solo por hoy",
                type = NotificationType.PROMOTION,
                timestamp = "Hace 2 horas"
            ),
            NotificationItem(
                id = "3",
                title = "Producto casi agotado",
                message = "Solo quedan 3 unidades de 'Monitor 4K Ultra HD'",
                type = NotificationType.SYSTEM,
                timestamp = "Hace 1 día",
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "¡Pedido confirmado!",
                message = "Tu pedido #1234 ha sido confirmado y está en preparación",
                type = NotificationType.ORDER,
                timestamp = "Hace 2 días",
                isRead = true
            ),
            NotificationItem(
                id = "5",
                title = "Nuevo producto disponible",
                message = "Check out the new iPhone 15 Pro Max - now available!",
                type = NotificationType.SYSTEM,
                timestamp = "Hace 3 días",
                isRead = true
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
    ) {
        NotificationHeader(
            userName = authUiState.user?.name ?: "Usuario",
            notificationCount = notifications.count { !it.isRead }
        )

        if (notifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = {
                            // TODO: Manejar click en notificación
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationHeader(
    userName: String,
    notificationCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Notificaciones",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
            }

            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .background(
                            color = colorResource(R.color.red_700),
                            shape = CircleShape
                        )
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Sin notificaciones",
                    tint = Color(0xFF718096),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getNotificationIconBackground(notification.type)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = notification.type.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = notification.timestamp,
                        fontSize = 12.sp,
                        color = Color(0xFF718096)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color(0xFF4A5568),
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!notification.isRead) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = colorResource(R.color.red_700),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "NUEVO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Sin notificaciones",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF718096)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No tienes notificaciones",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuando tengas nuevas notificaciones aparecerán aquí",
                fontSize = 14.sp,
                color = Color(0xFF718096)
            )
        }
    }
}

private fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.ORDER -> Icons.Default.LocalShipping
        NotificationType.PROMOTION -> Icons.Default.LocalOffer
        NotificationType.SYSTEM -> Icons.Default.Info
        NotificationType.CART -> Icons.Default.ShoppingCart
    }
}

private fun getNotificationIconBackground(type: NotificationType): Color {
    return when (type) {
        NotificationType.ORDER -> Color(0xFF10B981)
        NotificationType.PROMOTION -> Color(0xFFF59E0B)
        NotificationType.SYSTEM -> Color(0xFF3B82F6)
        NotificationType.CART -> Color(0xFFE53E3E)
    }
}
