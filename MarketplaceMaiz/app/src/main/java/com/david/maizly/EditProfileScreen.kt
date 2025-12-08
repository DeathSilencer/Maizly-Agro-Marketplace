package com.david.maizly

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
import androidx.navigation.NavController
import com.david.maizly.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val authState by viewModel.uiState.collectAsState()

    // Estado para el campo de texto, inicializado con el nombre actual
    var nombre by remember { mutableStateOf(currentUser?.displayName ?: "") }

    // --- Diálogos de Alerta ---
    if (authState.error != null) {
        StyledAlertDialog(
            title = "Error",
            message = authState.error!!,
            onDismiss = { viewModel.resetState() }
        )
    }
    // Diálogo de éxito que nos regresa
    if (authState.successMessage != null) {
        StyledAlertDialog(
            title = "¡Éxito!",
            message = authState.successMessage!!,
            onDismiss = {
                viewModel.resetState()
                navController.popBackStack() // Regresa al perfil
            }
        )
    }
    // --- Fin Diálogos ---

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // --- Campo de Nombre ---
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
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

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón de Guardar Cambios ---
            Button(
                onClick = {
                    // Llama al ViewModel para actualizar
                    viewModel.updateDisplayName(nombre)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                enabled = !authState.isUpdating // Deshabilitar si está cargando
            ) {
                if (authState.isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextoOscuro,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "Guardar Cambios",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }
            }
        }
    }
}