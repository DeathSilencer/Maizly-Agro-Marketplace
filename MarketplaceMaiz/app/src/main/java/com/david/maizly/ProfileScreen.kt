package com.david.maizly

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.david.maizly.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel) {

    // Obtenemos el usuario actual DE FORMA REACTIVA
    val currentUser = FirebaseAuth.getInstance().currentUser
    val authState by viewModel.uiState.collectAsState()

    // --- Lógica para el selector de imágenes ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it) // Llama al ViewModel para "subir" la foto
        }
    }

    // --- Diálogos de Alerta ---
    if (authState.error != null) {
        StyledAlertDialog(
            title = "Error",
            message = authState.error!!,
            onDismiss = { viewModel.resetState() }
        )
    }
    if (authState.successMessage != null) {
        StyledAlertDialog(
            title = "¡Éxito!",
            message = authState.successMessage!!,
            onDismiss = { viewModel.resetState() }
        )
    }
    // --- Fin Diálogos ---

    Scaffold(
        containerColor = MaizGrisClaro.copy(alpha = 0.5f), // Un fondo gris muy claro
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                // --- ¡NUEVO BOTÓN DE REGRESAR! ---
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN DE FOTO DE PERFIL ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaizGrisClaro)
                    .border(4.dp, Color.White, CircleShape)
                    .shadow(elevation = 4.dp, shape = CircleShape)
            ) {
                SubcomposeAsyncImage(
                    model = currentUser?.photoUrl, // Carga la URL de Firebase Auth
                    contentDescription = "Foto de perfil",
                    loading = { SkeletonPlaceholder(modifier = Modifier.fillMaxSize()) },
                    error = { // Fallback si no hay foto
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Foto de perfil",
                            tint = TextoSecundario,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Botón para cambiar foto
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaizAmarillo)
                        .clickable { imagePickerLauncher.launch("image/*") }, // Lanza el selector
                    contentAlignment = Alignment.Center
                ) {
                    if (authState.isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextoOscuro,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = TextoOscuro,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Nombre y Correo ---
            Text(
                text = currentUser?.displayName ?: "Sin Nombre",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Text(
                text = currentUser?.email ?: "Sin Correo",
                fontSize = 16.sp,
                color = TextoSecundario
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECCIÓN DE CONFIGURACIONES ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                // Opción 1: Editar Perfil
                SettingsItem(
                    text = "Editar Información del Perfil",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate(Screen.EditProfile.route) } // <-- ¡CAMBIO AQUÍ!
                )
                Divider(color = MaizGrisClaro, modifier = Modifier.padding(horizontal = 16.dp))

                // Opción 2: Mis Publicaciones (Futuro)
                SettingsItem(
                    text = "Mis Publicaciones",
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    onClick = { navController.navigate(Screen.MyPublications.route) } // <-- ¡CAMBIO AQUÍ!
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN DE CERRAR SESIÓN ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                SettingsItem(
                    text = "Cerrar Sesión",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    isDestructive = true, // Color rojo
                    onClick = {
                        viewModel.signOut()
                        // Limpia la pila de navegación y vuelve al Login
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ProductList.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Un Composable reutilizable para las opciones de la lista de configuración.
 */
@Composable
private fun SettingsItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (isDestructive) Color.Red.copy(alpha = 0.8f) else TextoOscuro

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = text, tint = contentColor)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, color = contentColor, fontSize = 16.sp)
        }
        if (!isDestructive) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextoSecundario)
        }
    }
}