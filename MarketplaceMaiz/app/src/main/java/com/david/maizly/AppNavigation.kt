package com.david.maizly

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Creamos las instancias de los ViewModels aquí para que sean compartidos
    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Ruta para Login
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        // Ruta para Registro
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

        // --- RUTAS DEL MARKETPLACE ---

        // Ruta para la lista de productos (Home)
        composable(route = Screen.ProductList.route) {
            ProductListScreen(
                navController = navController,
                productViewModel = productViewModel,
                cartViewModel = cartViewModel
            )
        }

        // Ruta para el detalle del producto
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                ProductDetailScreen(
                    navController = navController,
                    viewModel = productViewModel,
                    cartViewModel = cartViewModel,
                    authViewModel = authViewModel, // <-- ¡AÑADIDO!
                    productId = productId
                )
            }
        }

        // Ruta para añadir producto
        composable(route = Screen.AddProduct.route) {
            AddProductScreen(
                navController = navController,
                productViewModel = productViewModel
            )
        }

        // Ruta para el Perfil
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // Ruta para el Carrito
        composable(route = Screen.Cart.route) {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        // Ruta para el Checkout
        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        // --- ¡NUEVAS RUTAS! ---
        composable(route = Screen.ProducerList.route) {
            ProducerListScreen(
                navController = navController,
                productViewModel = productViewModel
            )
        }
        composable(
            route = Screen.ProducerDetail.route,
            arguments = listOf(navArgument("producerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val producerId = backStackEntry.arguments?.getString("producerId")
            if (producerId != null) {
                ProducerDetailScreen(
                    navController = navController,
                    viewModel = productViewModel,
                    producerId = producerId
                )
            }
        }
        // --- FIN NUEVAS RUTAS ---
        // --- ¡NUEVA RUTA PARA EDITAR! ---
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                EditProductScreen(
                    navController = navController,
                    productViewModel = productViewModel,
                    productId = productId
                )
            }
        }
        // --- ¡NUEVA RUTA PARA EDITAR PERFIL! ---
        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                navController = navController,
                viewModel = authViewModel // Reutiliza el AuthViewModel
            )
        }
        // --- ¡NUEVA RUTA PARA MIS PUBLICACIONES! ---
        composable(route = Screen.MyPublications.route) {
            MyPublicationsScreen(
                navController = navController,
                productViewModel = productViewModel // Reutiliza el ProductViewModel
            )
        }
    }
}
