package com.david.maizly

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.david.maizly.ui.theme.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- AÑADIDO: Escuchar al ViewModel ---
    val authState = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    // Estado para la visibilidad de la contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    // Efecto para navegar cuando el login es exitoso
    LaunchedEffect(authState.value.isSuccess) {
        if (authState.value.isSuccess) {
            navController.navigate(Screen.ProductList.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.resetState() // Limpiamos el estado
        }
    }

    // --- CAMBIO AQUÍ: Eliminamos el LaunchedEffect para el error ---

    // Usamos un Box para que el fondo MaizCrema se aplique
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaizCrema),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), // Padding lateral para todo
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- ✨ NUEVA IMAGEN BANNER ---
            Image(
                painter = painterResource(id = R.drawable.maiz_banner),
                contentDescription = "Banner de campo de maíz",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Título de la App ---
            Text(
                text = "Maíz Libre",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaizVerdeOscuro,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- Subtítulo / Saludo ---
            Text(
                text = "Bienvenido de vuelta",
                fontSize = 18.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(bottom = 32.dp)
            )

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
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // <-- CAMBIADO
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
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

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón de Entrar ---
            Button(
                onClick = { viewModel.loginUser(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                enabled = !authState.value.isLoading
            ) {
                Text(
                    text = "Entrar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Textos adicionales ---
            TextButton(onClick = { navController.navigate(Screen.Register.route) },
                enabled = !authState.value.isLoading
            ) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = MaizVerdeOscuro,
                    fontWeight = FontWeight.SemiBold,

                    )
            }
        }

        // --- SPINNER DE CARGA ---
        if (authState.value.isLoading) {
            CircularProgressIndicator(color = MaizVerdeOscuro)
        }

        // --- ¡NUEVO DIÁLOGO DE ERROR! ---
        if (authState.value.error != null) {
            StyledAlertDialog(
                title = "Error de Inicio de Sesión",
                message = authState.value.error!!,
                onDismiss = { viewModel.resetState() } // Al cerrar, resetea el VM
            )
        }
        // --- FIN DE DIÁLOGO ---
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    maizlyTheme {
        LoginScreen(
            navController = rememberNavController(),
            viewModel = AuthViewModel()
        )
    }
}