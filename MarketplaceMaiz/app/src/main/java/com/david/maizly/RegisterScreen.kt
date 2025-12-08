package com.david.maizly

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.david.maizly.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
// Eliminamos todos los imports de relocation, ExperimentalFoundationApi y focus

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    // Estados para los nuevos campos
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // --- Escuchar al ViewModel ---
    val authState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    // Estados para la visibilidad de las contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaizCrema),
        contentAlignment = Alignment.Center
    ) {
        // Añadimos verticalScroll para que no se corte en pantallas pequeñas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // <-- Dejamos el scroll normal
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp)) // Espacio superior

            // --- Título ---
            Text(
                text = "Crear cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaizVerdeOscuro,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- Subtítulo ---
            Text(
                text = "Únete a la comunidad",
                fontSize = 18.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- Campo de Nombre ---
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro,
                    unfocusedLabelColor = TextoSecundario
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Campo de Correo Electrónico ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro,
                    unfocusedLabelColor = TextoSecundario
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Campo de Contraseña ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                placeholder = { Text("6 caracteres mínimo") }, // <-- AÑADIDO
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // <-- CAMBIADO
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                // --- Icono de visibilidad ---
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility

                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro,
                    unfocusedLabelColor = TextoSecundario
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Campo de Confirmar Contraseña ---
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                placeholder = { Text("Repetir contraseña") }, // <-- AÑADIDO
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), // <-- CAMBIADO
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                // --- Icono de visibilidad ---
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility

                    val description = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    focusedLabelColor = MaizVerdeOscuro,
                    unfocusedLabelColor = TextoSecundario
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón de Registrarse ---
            Button(
                onClick = { viewModel.registerUser(nombre, email, password, confirmPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                enabled = !authState.value.isLoading
            ) {
                Text(
                    text = "Registrarme",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Texto para volver al Login ---
            TextButton(onClick = { navController.navigate(Screen.Login.route) },
                enabled = !authState.value.isLoading
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = MaizVerdeOscuro,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Espacio inferior
        }

        // --- SPINNER DE CARGA ---
        if (authState.value.isLoading) {
            CircularProgressIndicator(color = MaizVerdeOscuro)
        }

        // --- ¡NUEVO DIÁLOGO DE ERROR! ---
        if (authState.value.error != null) {
            StyledAlertDialog(
                title = "Error de Registro",
                message = authState.value.error!!,
                onDismiss = { viewModel.resetState() } // Al cerrar, resetea el VM
            )
        }

        // --- ¡NUEVO DIÁLOGO DE ÉXITO! ---
        if (authState.value.successMessage != null) {
            StyledAlertDialog(
                title = "¡Éxito!",
                message = authState.value.successMessage!!,
                onDismiss = {
                    // --- ¡CAMBIO AQUÍ! ---
                    // Al cerrar, navegamos al Login y reseteamos el VM
                    navController.navigate(Screen.Login.route) {
                        // Limpiamos la pila para que no pueda volver a "Registro"
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                    viewModel.resetState()
                }
            )
        }
        // --- FIN DE DIÁLOGOS ---
    }
}

// Para que tu Preview siga funcionando:
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    maizlyTheme {
        RegisterScreen(
            navController = rememberNavController(),
            viewModel = AuthViewModel()
        )
    }
}