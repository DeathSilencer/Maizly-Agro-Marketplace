package com.david.maizly

/**
 * Representa un producto dentro del carrito de compras.
 * Contiene el producto en sí y la cantidad seleccionada.
 */
data class CartItem(
    val product: Product,
    val quantity: Int
)