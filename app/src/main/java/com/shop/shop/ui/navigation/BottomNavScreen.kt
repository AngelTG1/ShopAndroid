package com.shop.shop.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.shop.shop.R
import com.shop.shop.ui.auth.AuthViewModel
import com.shop.shop.ui.cart.CartViewModel
import com.shop.shop.ui.cart.CartScreen
import com.shop.shop.ui.main.HomeScreen
import com.shop.shop.ui.notifications.NotificationsScreen
import com.shop.shop.ui.profile.ProfileScreen
import com.shop.shop.ui.products.ProductViewModel

// ✅ Sealed class para las pantallas del bottom navigation
sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: Int
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
    onNavigateToSubscription: () -> Unit = {}, // ✅ AGREGADO: Parámetro para navegar a suscripciones
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
                cartCount = cartUiState.cartCount,
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
                        onNavigateToProfile = { selectedTab = 3 },
                        onNavigateToAddProduct = onNavigateToAddProduct,
                        onNavigateToCart = { selectedTab = 2 },
                        onNavigateToProductDetail = onNavigateToProductDetail,
                        onNavigateToSubscription = onNavigateToSubscription, // ✅ PASADO: Navegación a suscripciones
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
                        onBack = { selectedTab = 0 },
                        onCheckout = {
                            // TODO: Implementar checkout
                        },
                        cartViewModel = cartViewModel
                    )
                }
                3 -> { // Perfil
                    ProfileScreen(
                        onBack = { selectedTab = 0 },
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
    // Envolvemos la NavigationBar en un Box para personalizar la altura
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(99.dp) // <- puedes cambiar esto a 88.dp o 72.dp si quieres ajustar
    ) {
        NavigationBar(
            containerColor = colorResource(R.color.white),
            contentColor = Color.White,
            tonalElevation = 4.dp,
             // Asegura que use todo el espacio del Box
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.padding(2.dp),
                    icon = {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(), // Asegura el centrado vertical
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                modifier = Modifier.size(27.dp), // Icono un poco más grande
                                tint = if (selectedTab == index) {
                                    colorResource(R.color.red_700)
                                } else {
                                    Color.Gray
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
                    label = null, // Puedes poner un Text aquí si quieres etiquetas
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.red_700),
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
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
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) {
                    Color(0xFF2E2E2E)
                } else {
                    Color(0xFF9E9E9E)
                }
            )

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
            .background(Color(0xFFF5F5F5))
    ) {
        BottomNavigationBar(
            items = bottomNavItems,
            selectedTab = selectedTab,
            cartCount = 3,
            onTabSelected = { selectedTab = it }
        )
    }
}