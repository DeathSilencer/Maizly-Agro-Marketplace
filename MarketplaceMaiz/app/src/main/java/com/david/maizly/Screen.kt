package com.david.maizly

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ProductList : Screen("product_list_screen")
    object Profile : Screen("profile_screen")
    object AddProduct : Screen("add_product_screen")
    object Cart : Screen("cart_screen")
    object Checkout : Screen("checkout_screen")

    object EditProfile : Screen("edit_profile_screen") // <-- AÑADIDO

    object MyPublications : Screen("my_publications_screen") // <-- AÑADIDO

    // --- ¡NUEVA RUTA! ---
    object EditProduct : Screen("edit_product/{productId}") {
        fun withId(id: String) = "edit_product/$id"
    }

    // --- ¡NUEVAS RUTAS! ---
    object ProducerList : Screen("producer_list_screen")
    object ProducerDetail : Screen("producer_detail/{producerId}") {
        fun withId(id: String) = "producer_detail/$id"
    }
    // --- FIN NUEVAS RUTAS ---

    // Ruta con argumento para el detalle
    object ProductDetail : Screen("product_detail/{productId}") {
        fun withId(id: String) = "product_detail/$id"


    }
}