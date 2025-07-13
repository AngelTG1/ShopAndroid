package com.shop.myamazon.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shop.myamazon.R
import com.shop.myamazon.ui.auth.AuthViewModel
import com.shop.myamazon.ui.cart.CartViewModel
import com.shop.myamazon.ui.cart.CartScreen
import com.shop.myamazon.ui.main.HomeScreen
import com.shop.myamazon.ui.notifications.NotificationsScreen
import com.shop.myamazon.ui.profile.ProfileScreen
import com.shop.myamazon.ui.products.ProductViewModel
import androidx.compose.ui.res.painterResource


// ✅ Sealed class para las pantallas del bottom navigation con tus iconos
sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: Int // ✅ Cambiar a Int para usar tus iconos de res/drawable
) {
    object Home : BottomNavScreen("bottom_home", "Inicio", R.drawable.solar_home)
    object Notifications : BottomNavScreen("bottom_notifications", "Notificaciones", R.drawable.solar_bell)
    object Cart : BottomNavScreen("bottom_cart", "Carrito", R.drawable.solar_cart)
    object Profile : BottomNavScreen("bottom_profile", "Perfil", R.drawable.solar_perfil)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithBottomNav(
    onLogout: () -> Unit,
    onNavigateToAddProduct: () -> Unit = {},
    onNavigateToMyProducts: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val cartUiState by cartViewModel.uiState.collectAsState()

    // ✅ Lista de pantallas para el bottom navigation
    val bottomNavItems = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Notifications,
        BottomNavScreen.Cart,
        BottomNavScreen.Profile
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                selectedTab = selectedTab,
                cartCount = cartUiState.cartCount, // ✅ Badge en el carrito
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ✅ Contenido según la pestaña seleccionada
            when (selectedTab) {
                0 -> { // Home
                    HomeScreen(
                        onLogout = onLogout,
                        onNavigateToProfile = { selectedTab = 3 }, // Cambiar a pestaña Profile
                        onNavigateToAddProduct = onNavigateToAddProduct,
                        onNavigateToCart = { selectedTab = 2 }, // Cambiar a pestaña Cart
                        onNavigateToProductDetail = onNavigateToProductDetail,
                        authViewModel = authViewModel,
                        productViewModel = productViewModel,
                        cartViewModel = cartViewModel
                    )
                }
                1 -> { // Notificaciones
                    NotificationsScreen(
                        authViewModel = authViewModel
                    )
                }
                2 -> { // Carrito
                    CartScreen(
                        onBack = { selectedTab = 0 }, // Volver a Home
                        onCheckout = {
                            // TODO: Implementar checkout
                        },
                        cartViewModel = cartViewModel
                    )
                }
                3 -> { // Perfil
                    ProfileScreen(
                        onBack = { selectedTab = 0 }, // Volver a Home
                        onLogout = onLogout,
                        onNavigateToMyProducts = onNavigateToMyProducts,
                        viewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    items: List<BottomNavScreen>,
    selectedTab: Int,
    cartCount: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = colorResource(R.color.white),
        contentColor = Color.White,
        modifier = Modifier.height(70.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Box {
                        // ✅ Cambiar a painterResource para usar tus iconos
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(28.dp),
                            // ✅ OPCIÓN 1: Cambiar color directamente en el Icon
                            tint = if (selectedTab == index) {
                                colorResource(R.color.red_700)
                            } else {
                                Color.Gray    // ✅ Color cuando no está seleccionado
                            }
                        )

                        if (item == BottomNavScreen.Cart && cartCount > 0) {
                            Badge(
                                modifier = Modifier.offset(x = 14.dp, y = (-14).dp),
                                containerColor = Color.Black
                            ) {
                                Text(
                                    text = if (cartCount > 99) "99+" else cartCount.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                },
                label = null,
                // ✅ OPCIÓN 2: Cambiar colores usando NavigationBarItemDefaults
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Red,        // ✅ Ícono cuando seleccionado
                    unselectedIconColor = Color.Blue,     // ✅ Ícono cuando no seleccionado
                    indicatorColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
private fun CustomNavItem(
    item: BottomNavScreen,
    isSelected: Boolean,
    cartCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box {
            // ✅ Usar Icon con painterResource para iconos personalizados
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) {
                    Color(0xFF2E2E2E) // ✅ Negro cuando seleccionado
                } else {
                    Color(0xFF9E9E9E) // ✅ Gris cuando no seleccionado
                }
            )

            // ✅ Badge para el carrito si hay items
            if (item == BottomNavScreen.Cart && cartCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 12.dp, y = (-6).dp)
                        .background(
                            color = Color(0xFFE53E3E),
                            shape = CircleShape
                        )
                        .size(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartCount > 9) "9+" else cartCount.toString(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ✅ PREVIEW con iconos por defecto (fallback)
@Preview(showBackground = true)
@Composable
fun CustomBottomNavPreview() {
    var selectedTab by remember { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Notifications,
        BottomNavScreen.Cart,
        BottomNavScreen.Profile
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        CustomBottomNavigationBar(
            items = bottomNavItems,
            selectedTab = selectedTab,
            cartCount = 3, // Con badge para probar
            onTabSelected = {
                val it = 0
                selectedTab = it
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ✅ PREVIEW con diferentes estados
@Preview(showBackground = true)
@Composable
fun CustomBottomNavStatesPreview() {
    Column {
        // Estado 1: Home seleccionado
        var selectedTab1 by remember { mutableStateOf(0) }
        CustomBottomNavigationBar(
            items = listOf(
                BottomNavScreen.Home,
                BottomNavScreen.Notifications,
                BottomNavScreen.Cart,
                BottomNavScreen.Profile
            ),
            selectedTab = selectedTab1,
            cartCount = 0,
            onTabSelected = {
                val it = 0
                selectedTab1 = it
            },
            modifier = Modifier.align(Alignment.BottomCenter as Alignment.Horizontal)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado 2: Notificaciones seleccionado
        var selectedTab2 by remember { mutableStateOf(1) }
        CustomBottomNavigationBar(
            items = listOf(
                BottomNavScreen.Home,
                BottomNavScreen.Notifications,
                BottomNavScreen.Cart,
                BottomNavScreen.Profile
            ),
            selectedTab = selectedTab2,
            cartCount = 0,
            onTabSelected = {
                val it = 0
                selectedTab2 = it
            },
            modifier = Modifier.align(Alignment.BottomCenter as Alignment.Horizontal)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado 3: Cart seleccionado con badge
        var selectedTab3 by remember { mutableIntStateOf(2) }
        CustomBottomNavigationBar(
            items = listOf(
                BottomNavScreen.Home,
                BottomNavScreen.Notifications,
                BottomNavScreen.Cart,
                BottomNavScreen.Profile
            ),
            selectedTab = selectedTab3,
            cartCount = 5, // Con badge
            onTabSelected = {
                val it = 0
                selectedTab3 = it
            },
            modifier = Modifier.align(Alignment.BottomCenter as Alignment.Horizontal)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado 4: Perfil seleccionado
        var selectedTab4 by remember { mutableIntStateOf(3) }
        CustomBottomNavigationBar(
            items = listOf(
                BottomNavScreen.Home,
                BottomNavScreen.Notifications,
                BottomNavScreen.Cart,
                BottomNavScreen.Profile
            ),
            selectedTab = selectedTab4,
            cartCount = 0,
            onTabSelected = {
                val it = 0
                selectedTab4 = it
            },
            modifier = Modifier.align(Alignment.BottomCenter as Alignment.Horizontal)
        )
    }
}

@Composable
fun CustomBottomNavigationBar(
    items: List<BottomNavScreen>,
    selectedTab: Int,
    cartCount: Int,
    onTabSelected: () -> Unit,
    modifier: Modifier
) {
    TODO("Not yet implemented")
}