package com.david.maizly

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions // <-- ¡NUEVO IMPORT!
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction // <-- ¡NUEVO IMPORT!
import androidx.compose.ui.text.input.KeyboardType // <-- ¡NUEVO IMPORT!
import androidx.compose.ui.text.style.TextAlign // <-- ¡NUEVO IMPORT!
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.david.maizly.ui.theme.*
import java.util.Locale

/**
 * Diálogo emergente para seleccionar la cantidad de un producto.
 */
@Composable
fun QuantityDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    // --- ¡CAMBIO! ---
    // El estado ahora es un String para permitir la entrada por teclado.
    var quantityText by remember { mutableStateOf("1") }

    // --- ¡NUEVO! Validación en tiempo real ---
    val quantityInt = quantityText.toIntOrNull()

    val (isError, errorMessage) = when {
        quantityText.isBlank() -> {
            true to "No puede estar vacío"
        }
        quantityInt == null -> {
            true to "Número inválido"
        }
        quantityInt <= 0 -> {
            true to "Debe ser al menos 1"
        }
        quantityInt > product.stock -> {
            true to "Stock insuficiente (Máx: ${product.stock})"
        }
        else -> {
            false to null // Sin error
        }
    }
    // --- FIN DE VALIDACIÓN ---

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = MaizCrema,
        shape = RoundedCornerShape(12.dp),

        // --- Título ---
        title = {
            Text(
                text = "Seleccionar Cantidad",
                fontWeight = FontWeight.Bold,
                color = MaizVerdeOscuro
            )
        },

        // --- Contenido (Selector) ---
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = product.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextoOscuro
                )
                Text(
                    text = "Stock disponible: ${product.stock}",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- ¡NUEVOS CONTROLES DE CANTIDAD! ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Botón de restar
                    IconButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 1
                            if (current > 1) quantityText = (current - 1).toString()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, MaizGrisClaro, CircleShape),
                        enabled = (quantityInt ?: 1) > 1
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar", tint = TextoOscuro)
                    }

                    // --- CAMPO DE TEXTO PARA CANTIDAD ---
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = {
                            // Solo permite números y un máximo de 4 dígitos
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                quantityText = it
                            }
                        },
                        modifier = Modifier.width(90.dp),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = isError, // Muestra el borde rojo si hay error
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = MaizGrisClaro,
                            focusedIndicatorColor = MaizVerdeOscuro,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextoOscuro,
                            unfocusedTextColor = TextoOscuro,
                            errorIndicatorColor = Color.Red.copy(alpha = 0.7f),
                            errorContainerColor = Color.White
                        )
                    )

                    // Botón de sumar
                    IconButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 0
                            if (current < product.stock) quantityText = (current + 1).toString()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, MaizGrisClaro, CircleShape),
                        enabled = (quantityInt ?: 0) < product.stock // Deshabilita si llega al stock
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Sumar",
                            tint = if ((quantityInt ?: 0) < product.stock) MaizVerdeOscuro else Color.Gray
                        )
                    }
                }

                // --- ¡NUEVO! Mensaje de Error ---
                if (isError && errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // --- FIN DE MENSAJE DE ERROR ---
            }
        },

        // --- Botones de Acción ---
        confirmButton = {
            Button(
                onClick = {
                    // quantityInt no será nulo porque el botón está deshabilitado si hay error
                    onConfirm(quantityInt!!)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                shape = RoundedCornerShape(8.dp),
                enabled = !isError // Deshabilita el botón si hay un error
            ) {
                Text("Añadir", color = TextoOscuro)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar", color = MaizVerdeOscuro)
            }
        }
    )
}
