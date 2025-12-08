package com.david.maizly

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted // <-- ¡ARREGLO: IMPORT AÑADIDO!
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Estado del Carrito
data class CartState(
    val items: List<CartItem> = emptyList(),
    val error: String? = null, // Para errores de stock
    val successMessage: String? = null // <-- AÑADIDO
)

class CartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CartState())
    val uiState: StateFlow<CartState> = _uiState.asStateFlow()

    // --- ¡NUEVO! ---
    // Estado para el flujo de pago
    private val _checkoutSuccess = MutableStateFlow(false)
    val checkoutSuccess: StateFlow<Boolean> = _checkoutSuccess.asStateFlow()
    // --- FIN NUEVO ---

    // Calcula el precio total
    val totalPrice: StateFlow<Double> = _uiState.map { state ->
        state.items.sumOf { it.product.precio * it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // <-- Esto fallaba
        initialValue = 0.0
    )

    // Formateador de moneda
    val formattedTotalPrice: StateFlow<String> = totalPrice.map { price ->
        NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(price)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // <-- Esto fallaba
        initialValue = "$0.00"
    )

    // --- ¡FUNCIÓN REEMPLAZADA Y MEJORADA! ---
// Ahora acepta una cantidad específica
    fun addMultipleToCart(product: Product, quantityToAdd: Int) {
        val currentItems = _uiState.value.items
        val existingItem = currentItems.find { it.product.id == product.id }

        val newQuantity = (existingItem?.quantity ?: 0) + quantityToAdd

        // 1. Comprobar si la *nueva* cantidad total supera el stock
        if (newQuantity > product.stock) {
            val available = product.stock - (existingItem?.quantity ?: 0)
            _uiState.value = _uiState.value.copy(
                error = if (available <= 0) {
                    "¡Ups! Ya tienes todo el stock de este producto en tu carrito."
                } else {
                    "No puedes añadir $quantityToAdd. Solo ${if (available == 1) "queda 1 disponible" else "quedan $available disponibles"}."
                }
            )
            return
        }

        // 2. Si el stock es válido, añadir o actualizar el item
        val updatedList: List<CartItem>
        if (existingItem != null) {
            // Si ya existe, actualiza la cantidad
            updatedList = currentItems.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
        } else {
            // Si es nuevo, añádelo a la lista
            updatedList = currentItems + CartItem(product = product, quantity = quantityToAdd)
        }

        val successMsg = if (quantityToAdd > 1) {
            "¡Añadiste $quantityToAdd ${product.nombre}s al carrito!"
        } else {
            "¡Añadiste $quantityToAdd ${product.nombre} al carrito!"
        }

        _uiState.value = _uiState.value.copy(
            items = updatedList,
            successMessage = successMsg // Prepara el mensaje de éxito
        )
    }

    fun increaseQuantity(productId: String) {
        val item = _uiState.value.items.find { it.product.id == productId }
        if (item != null) {
            // Llama a la nueva función con cantidad 1
            // La lógica de stock ya está dentro de addMultipleToCart
            addMultipleToCart(item.product, 1)
        }
    }

    fun decreaseQuantity(productId: String) {
        val currentItems = _uiState.value.items
        val updatedItems = currentItems.mapNotNull { item ->
            if (item.product.id == productId) {
                if (item.quantity > 1) {
                    item.copy(quantity = item.quantity - 1)
                } else {
                    null // Elimina el item si la cantidad llega a 0
                }
            } else {
                item
            }
        }
        _uiState.value = _uiState.value.copy(items = updatedItems)
    }

    fun removeFromCart(productId: String) {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items.filter { it.product.id != productId }
        )
    }

    // Renombrada para ser más clara
    fun resetCartMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    // --- ¡NUEVAS FUNCIONES! ---
    fun confirmCheckout() {
        // Simula un pago/confirmación
        viewModelScope.launch {
            delay(1500) // Simula espera de red
            _checkoutSuccess.value = true
        }
    }

    fun clearCart() {
        _uiState.value = CartState() // Resetea el carrito
    }

    fun resetCheckoutSuccess() {
        _checkoutSuccess.value = false
    }
    // --- FIN NUEVAS FUNCIONES ---
}