package com.shop.myamazon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shop.myamazon.R
import com.shop.myamazon.data.remote.models.User
import androidx.compose.ui.res.painterResource

@Composable
fun UserHeader(
    user: User?,
    cartCount: Int = 0,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onPlanesClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    cartViewModel: com.shop.myamazon.ui.cart.CartViewModel? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    // ✅ Obtener configuración de pantalla para valores adaptativos
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // ✅ Valores adaptativos (optimizado para Samsung A36)
    val isCompactHeight = screenHeight < 650.dp  // ✅ Ajustado para A36
    val isCompactWidth = screenWidth < 400.dp
    val isMediumScreen = screenWidth >= 400.dp && screenWidth < 500.dp  // ✅ Para A36

    // ✅ Padding adaptativo optimizado para diferentes dispositivos
    val horizontalPadding = when {
        screenWidth < 360.dp -> 12.dp     // ✅ Pantallas muy pequeñas
        screenWidth < 400.dp -> 16.dp     // ✅ Pantallas pequeñas
        screenWidth < 500.dp -> 20.dp     // ✅ Samsung A36 y similares
        screenWidth < 600.dp -> 24.dp     // ✅ Pantallas medianas
        else -> 28.dp                     // ✅ Pantallas grandes
    }

    val topPadding = when {
        screenHeight < 600.dp -> 8.dp     // ✅ Pantallas muy compactas
        screenHeight < 700.dp -> 12.dp    // ✅ Samsung A36 y similares
        screenHeight < 800.dp -> 16.dp    // ✅ Pantallas medianas
        else -> 20.dp                     // ✅ Pantallas grandes
    }

    val bottomPadding = when {
        screenHeight < 600.dp -> 8.dp     // ✅ Pantallas muy compactas
        screenHeight < 700.dp -> 12.dp    // ✅ Samsung A36 y similares
        else -> 16.dp                     // ✅ Pantallas normales
    }

    // ✅ Tamaños de texto adaptativos
    val nameTextSize = when {
        isCompactWidth -> 16.sp
        isMediumScreen -> 17.sp           // ✅ Específico para A36
        else -> 18.sp
    }

    val roleTextSize = when {
        isCompactWidth -> 12.sp
        isMediumScreen -> 13.sp           // ✅ Específico para A36
        else -> 14.sp
    }

    // ✅ Tamaño de iconos adaptativos
    val iconSize = when {
        isCompactWidth -> 20.dp
        isMediumScreen -> 22.dp           // ✅ Específico para A36
        else -> 24.dp
    }

    val avatarSize = when {
        isCompactWidth -> 40.dp
        isMediumScreen -> 44.dp           // ✅ Específico para A36
        else -> 48.dp
    }

    val cartButtonSize = when {
        isCompactWidth -> 40.dp
        isMediumScreen -> 44.dp           // ✅ Específico para A36
        else -> 48.dp
    }

    LaunchedEffect(user?.id) {
        if (user != null && cartViewModel != null) {
            println("👤 Usuario cambió a: ${user.name} (ID: ${user.id})")
            cartViewModel.refreshCart()
        }
    }

    // ✅ Card que llega hasta el borde superior
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 0.dp,  // ✅ Sin padding horizontal para llegar a los bordes
                vertical = 0.dp     // ✅ Sin padding vertical para llegar arriba
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompactHeight) 2.dp else 4.dp
        ),
        shape = RoundedCornerShape(
            topStart = 0.dp,    // ✅ Sin bordes redondeados arriba
            topEnd = 0.dp,      // ✅ Sin bordes redondeados arriba
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = if (isCompactHeight) 12.dp else 16.dp,  // ✅ Padding interno más generoso
                ).padding(top = if (isCompactHeight) 8.dp else 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Información del usuario con espacio optimizado
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hello, ${user?.name ?: "Usuario"}",
                    fontSize = nameTextSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ✅ Espaciado mínimo entre textos
                Spacer(modifier = Modifier.height(
                    if (isCompactHeight) 1.dp else 2.dp
                ))

                Text(
                    text = user?.role ?: "Cliente",
                    fontSize = roleTextSize,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF718096),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ✅ Sección de acciones (carrito + avatar) - Optimizada
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    if (isCompactWidth) 6.dp else 8.dp
                )
            ) {
                // ✅ Botón del carrito con badge - Tamaño adaptativo
                Box {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .background(
                                color = colorResource(R.color.red_700).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .size(cartButtonSize)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.solar_cart),
                            contentDescription = "Carrito",
                            tint = colorResource(R.color.red_700),
                            modifier = Modifier.size(iconSize)
                        )
                    }

                    // ✅ Badge adaptativo
                    if (cartCount > 0) {
                        val badgeSize = if (isCompactWidth) 18.dp else 20.dp
                        val badgeOffset = if (isCompactWidth) 10.dp else 12.dp

                        Box(
                            modifier = Modifier
                                .offset(x = badgeOffset, y = (-badgeOffset))
                                .background(
                                    color = colorResource(R.color.red_700),
                                    shape = CircleShape
                                )
                                .size(badgeSize),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cartCount > 99) "99+" else cartCount.toString(),
                                color = Color.White,
                                fontSize = if (isCompactWidth) 8.sp else 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ✅ Avatar con menú dropdown - Tamaño adaptativo
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(avatarSize)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                                .background(colorResource(R.color.red_700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.name?.take(1)?.uppercase() ?: "U",
                                color = Color.White,
                                fontSize = if (isCompactWidth) 16.sp else 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // ✅ Menú dropdown adaptativo
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        // ✅ Opción Ver Perfil
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Ver perfil",
                                        tint = Color(0xFF4A5568),
                                        modifier = Modifier.size(
                                            if (isCompactWidth) 16.dp else 18.dp
                                        )
                                    )
                                    Text(
                                        text = "Ver perfil",
                                        color = Color(0xFF2D3748),
                                        fontSize = if (isCompactWidth) 12.sp else 14.sp
                                    )
                                }
                            },
                            onClick = {
                                showMenu = false
                                onProfileClick()
                            }
                        )

                        // ✅ Opción Mi Carrito
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Mi carrito",
                                        tint = Color(0xFF4A5568),
                                        modifier = Modifier.size(
                                            if (isCompactWidth) 16.dp else 18.dp
                                        )
                                    )
                                    Text(
                                        text = "Mi carrito",
                                        color = Color(0xFF2D3748),
                                        fontSize = if (isCompactWidth) 12.sp else 14.sp
                                    )
                                    // ✅ Badge en el menú
                                    if (cartCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "($cartCount)",
                                            color = colorResource(R.color.red_700),
                                            fontSize = if (isCompactWidth) 10.sp else 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            },
                            onClick = {
                                showMenu = false
                                onCartClick()
                            }
                        )

                        // ✅ Opción Ver Planes
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalPlay,
                                        contentDescription = "Ver Planes",
                                        tint = Color(0xFF4A5568),
                                        modifier = Modifier.size(
                                            if (isCompactWidth) 16.dp else 18.dp
                                        )
                                    )
                                    Text(
                                        text = "Ver Planes",
                                        color = Color(0xFF2D3748),
                                        fontSize = if (isCompactWidth) 12.sp else 14.sp
                                    )
                                }
                            },
                            onClick = {
                                showMenu = false
                                onPlanesClick()
                            }
                        )

                        // ✅ Línea divisoria
                        Divider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // ✅ Opción Cerrar Sesión
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Cerrar sesión",
                                        tint = Color(0xFFE53E3E),
                                        modifier = Modifier.size(
                                            if (isCompactWidth) 16.dp else 18.dp
                                        )
                                    )
                                    Text(
                                        text = "Cerrar sesión",
                                        color = Color(0xFFE53E3E),
                                        fontSize = if (isCompactWidth) 12.sp else 14.sp
                                    )
                                }
                            },
                            onClick = {
                                showMenu = false
                                onLogoutClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

// ✅ Preview adaptativo
@Preview(showBackground = true, heightDp = 100)
@Composable
fun UserHeaderPreview() {
    val sampleUser = User(
        id = 1,
        uuid = "123",
        name = "Angel",
        lastName = "García",
        email = "angel@example.com",
        phone = "123456789",
        role = "memberships"
    )

    UserHeader(
        user = sampleUser,
        cartCount = 3,
        onProfileClick = { println("Ver perfil") },
        onCartClick = { println("Ver carrito") },
        onPlanesClick = { println("Ver Planes") },
        onLogoutClick = { println("Cerrar sesión") }
    )
}