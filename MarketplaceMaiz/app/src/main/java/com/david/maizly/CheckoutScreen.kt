package com.david.maizly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.david.maizly.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    // Estados para los campos de texto
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }

    // Estados del ViewModel
    val formattedTotal by cartViewModel.formattedTotalPrice.collectAsState()
    val checkoutSuccess by cartViewModel.checkoutSuccess.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    // Diálogo de éxito
    if (checkoutSuccess) {
        StyledAlertDialog(
            title = "¡Pedido Exitoso!",
            // --- ¡AQUÍ ESTÁ EL ARREGLO! ---
            message = "Tu pedido ha sido confirmado y se está procesando. ¡Gracias por tu compra!",
            onDismiss = {
                cartViewModel.clearCart() // Limpia el carrito
                cartViewModel.resetCheckoutSuccess() // Resetea el estado
                // Navega a la lista de productos y limpia toda la pila anterior
                navController.navigate(Screen.ProductList.route) {
                    popUpTo(Screen.ProductList.route) { inclusive = true }
                }
            }
        )
    }

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Pedido", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()) // Hacemos la pantalla "scrollable"
                .padding(16.dp)
        ) {
            // --- Resumen del Pedido ---
            Text(
                text = "Resumen del Pedido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total a Pagar:",
                        fontSize = 18.sp,
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
            }
            Spacer(modifier = Modifier.height(24.dp))

            // --- Campos Falsos de Dirección y Pago ---
            Text(
                text = "Dirección de Envío",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Dirección
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección (Calle y Número)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Ciudad
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Ciudad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Código Postal
            OutlinedTextField(
                value = zipCode,
                onValueChange = { zipCode = it },
                label = { Text("Código Postal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Método de Pago",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Nombre Tarjeta
            OutlinedTextField(
                value = cardName,
                onValueChange = { cardName = it },
                label = { Text("Nombre en la Tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Número Tarjeta
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = { Text("Número de Tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón de Confirmar Pedido ---
            Button(
                onClick = {
                    // Validamos (muy simple) que los campos no estén vacíos
                    if (address.isNotBlank() && city.isNotBlank() && zipCode.isNotBlank() && cardName.isNotBlank() && cardNumber.isNotBlank()) {
                        isLoading = true
                        cartViewModel.confirmCheckout()
                    }
                    // (En una app real, aquí se mostraría un error si los campos están vacíos)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                enabled = !isLoading // Deshabilitar si está cargando
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextoOscuro,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "Confirmar Pedido ($formattedTotal)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}