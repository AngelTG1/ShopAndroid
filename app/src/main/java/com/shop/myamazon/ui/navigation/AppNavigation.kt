package com.shop.myamazon.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shop.myamazon.ui.auth.AuthViewModel
import com.shop.myamazon.ui.auth.LoginScreen
import com.shop.myamazon.ui.auth.RegisterScreen
import com.shop.myamazon.ui.products.AddProductScreen
import com.shop.myamazon.ui.products.AddProductViewModel
import com.shop.myamazon.ui.products.MyProductsScreen
import com.shop.myamazon.ui.products.MyProductsViewModel
import com.shop.myamazon.ui.products.ProductDetailScreen
import com.shop.myamazon.ui.products.ProductDetailViewModel
import com.shop.myamazon.ui.products.ProductViewModel
import com.shop.myamazon.ui.cart.CartViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.setCartViewModel(cartViewModel)
    }

    val startDestination = if (uiState.isLoggedIn) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        // ✅ NUEVA: Pantalla principal con bottom navigation
        composable("main") {
            val productViewModel: ProductViewModel = viewModel()

            MainScreenWithBottomNav(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onNavigateToAddProduct = {
                    navController.navigate("add_product")
                },
                onNavigateToMyProducts = {
                    navController.navigate("my_products")
                },
                onNavigateToProductDetail = { productUuid ->
                    navController.navigate("product_detail/$productUuid")
                },
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                productViewModel = productViewModel
            )
        }

        // ✅ Pantalla de detalle de producto
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val productDetailViewModel: ProductDetailViewModel = viewModel()

            ProductDetailScreen(
                productId = productId,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    // ✅ Navegar a main y cambiar a pestaña de carrito
                    navController.navigate("main") {
                        popUpTo("product_detail/$productId") { inclusive = true }
                    }
                    // TODO: Implementar cambio directo a pestaña carrito
                },
                onNavigateToProduct = { newProductId ->
                    navController.navigate("product_detail/$newProductId")
                },
                viewModel = productDetailViewModel
            )
        }

        // ✅ Pantalla para agregar productos
        composable("add_product") {
            val addProductViewModel: AddProductViewModel = viewModel()

            AddProductScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProductAdded = {
                    navController.popBackStack()
                },
                addProductViewModel = addProductViewModel
            )
        }

        // ✅ Pantalla de mis productos
        composable("my_products") {
            val myProductsViewModel: MyProductsViewModel = viewModel()

            MyProductsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEditProduct = { product ->
                    // TODO: Implementar edición
                },
                onAddProduct = {
                    navController.navigate("add_product")
                },
                onViewProduct = { product ->
                    navController.navigate("product_detail/${product.uuid}")
                },
                myProductsViewModel = myProductsViewModel
            )
        }
    }
}