package com.david.maizly

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage       // <-- Lo quitamos
import coil.compose.SubcomposeAsyncImage // <-- ¡CAMBIO AQUÍ!
import com.david.maizly.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val formattedTotal by cartViewModel.formattedTotalPrice.collectAsState()

    // Diálogo de error para el stock
    if (cartState.error != null) {
        StyledAlertDialog(
            title = "Stock Insuficiente",
            message = cartState.error ?: "No se pudo añadir el producto.",
            onDismiss = { cartViewModel.resetCartMessages() } // <-- Función actualizada
        )
    }

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (cartState.items.isEmpty()) {
            // --- VISTA DE CARRITO VACÍO ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    fontSize = 20.sp,
                    color = TextoSecundario
                )
            }
        } else {
            // --- VISTA DE CARRITO LLENO ---
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {

                // La lista de productos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(cartState.items) { item ->
                        CartItemView(
                            item = item,
                            onIncrease = { cartViewModel.increaseQuantity(item.product.id) },
                            onDecrease = { cartViewModel.decreaseQuantity(item.product.id) },
                            onRemove = { cartViewModel.removeFromCart(item.product.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- PIE DE PÁGINA (Total y Botón) ---
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        CartSummary(
                            formattedTotal = formattedTotal,
                            onCheckoutClick = {
                                navController.navigate(Screen.Checkout.route)
                            }
                        )
                        // Añadimos un espacio extra al final para que no se pegue al fondo
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemView(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- ¡CAMBIO AQUÍ! ---
            SubcomposeAsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.nombre,
                loading = { // Lo que se muestra mientras carga
                    SkeletonPlaceholder(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    )
                },
                // --- FIN DEL CAMBIO ---
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.product.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${item.product.precio}",
                    fontSize = 16.sp,
                    color = TextoSecundario
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Controles de Cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Botón de restar
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar", tint = TextoOscuro)
                    }

                    Text(
                        text = "${item.quantity}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    // Botón de sumar
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(28.dp),
                        // Deshabilitar si se alcanza el stock
                        enabled = item.quantity < item.product.stock
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Sumar",
                            tint = if (item.quantity < item.product.stock) MaizVerdeOscuro else Color.Gray
                        )
                    }
                }
            }

            // Botón de Eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun CartSummary(
    formattedTotal: String,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila del Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextoSecundario
                )
                Text(
                    text = formattedTotal,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Pagar
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo)
            ) {
                Text(
                    text = "Proceder al Pago",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
            }
        }
    }
}