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
    onCartClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    cartViewModel: com.shop.myamazon.ui.cart.CartViewModel? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(user?.id) {
        if (user != null && cartViewModel != null) {
            println("👤 Usuario cambió a: ${user.name} (ID: ${user.id})")
            cartViewModel.refreshCart()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hello, ${user?.name ?: "Usuario"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A202C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = user?.role ?: "Cliente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF718096),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ✅ Sección de acciones (carrito + avatar)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ✅ Botón del carrito con badge
                Box {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .background(
                                color = colorResource(R.color.red_700).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.solar_cart),
                            contentDescription = "Carrito",
                            tint = colorResource(R.color.red_700),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Badge con el número de items - Solo mostrar si cartCount > 0
                    if (cartCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = 12.dp, y = (-12).dp)
                                .background(
                                    color = colorResource(R.color.red_700),
                                    shape = CircleShape
                                )
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (cartCount > 99) "99+" else cartCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Avatar con menú dropdown
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colorResource(R.color.red_700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.name?.take(1)?.uppercase() ?: "U",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Menú dropdown
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        // Opción Ver Perfil
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Ver perfil",
                                        tint = Color(0xFF4A5568)
                                    )
                                    Text(
                                        text = "Ver perfil",
                                        color = Color(0xFF2D3748),
                                        fontSize = 14.sp
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
                                        tint = Color(0xFF4A5568)
                                    )
                                    Text(
                                        text = "Mi carrito",
                                        color = Color(0xFF2D3748),
                                        fontSize = 14.sp
                                    )
                                    // ✅ Mostrar cantidad en el menú también
                                    if (cartCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "($cartCount)",
                                            color = colorResource(R.color.red_700),
                                            fontSize = 12.sp,
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

                        // Línea divisoria
                        Divider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // Opción Cerrar Sesión
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Cerrar sesión",
                                        tint = Color(0xFFE53E3E)
                                    )
                                    Text(
                                        text = "Cerrar sesión",
                                        color = Color(0xFFE53E3E),
                                        fontSize = 14.sp
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

// Preview para testing
@Preview(showBackground = true)
@Composable
fun UserHeaderPreview() {
    val sampleUser = User(
        id = 1,
        uuid = "123",
        name = "Vanessa",
        lastName = "García",
        email = "vanessa@example.com",
        phone = "123456789",
        role = "Cliente"
    )

    UserHeader(
        user = sampleUser,
        cartCount = 3, // ✅ Ejemplo con 3 items en el carrito
        onProfileClick = { println("Ver perfil") },
        onCartClick = { println("Ver carrito") }, // ✅ Nueva acción
        onLogoutClick = { println("Cerrar sesión") }
    )
}