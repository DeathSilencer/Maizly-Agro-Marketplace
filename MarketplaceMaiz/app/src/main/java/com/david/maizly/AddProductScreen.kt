package com.david.maizly

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.david.maizly.ui.theme.*
import androidx.compose.ui.draw.shadow // <-- ¡AQUÍ ESTÁ EL ARREGLO!


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") } // <-- ¡NUEVO!
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Estado del ViewModel
    val state by productViewModel.addProductState.collectAsState()

    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Publicar Producto", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Selector de Imagen ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaizGrisClaro)
                        .border(1.dp, MaizVerdeOscuro, RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Añadir foto",
                            tint = MaizVerdeOscuro,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
                TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Seleccionar Imagen", color = MaizVerdeOscuro, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Nombre del Producto ---
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MaizVerdeOscuro, unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextoOscuro, unfocusedTextColor = TextoOscuro,
                        focusedLabelColor = MaizVerdeOscuro, unfocusedLabelColor = TextoSecundario
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Fila para Precio y Stock ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Campo Precio ---
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (Ej: 25.00)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MaizVerdeOscuro, unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextoOscuro, unfocusedTextColor = TextoOscuro,
                            focusedLabelColor = MaizVerdeOscuro, unfocusedLabelColor = TextoSecundario
                        )
                    )

                    // --- ¡NUEVO! Campo Stock ---
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MaizVerdeOscuro, unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextoOscuro, unfocusedTextColor = TextoOscuro,
                            focusedLabelColor = MaizVerdeOscuro, unfocusedLabelColor = TextoSecundario
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Descripción ---
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MaizVerdeOscuro, unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextoOscuro, unfocusedTextColor = TextoOscuro,
                        focusedLabelColor = MaizVerdeOscuro, unfocusedLabelColor = TextoSecundario
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- Botón de Publicar ---
                Button(
                    onClick = {
                        productViewModel.addProduct(nombre, precio, descripcion, stock, imageUri)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "Publicar Producto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }
            }

            // --- Spinner de Carga ---
            if (state.isLoading) {
                CircularProgressIndicator(color = MaizVerdeOscuro)
            }

            // --- Diálogo de Error ---
            if (state.error != null) {
                StyledAlertDialog(
                    title = "Error al Publicar",
                    message = state.error!!,
                    onDismiss = { productViewModel.resetAddProductState() }
                )
            }

            // --- Diálogo de Éxito ---
            if (state.isSuccess) {
                StyledAlertDialog(
                    title = "¡Éxito!",
                    message = state.successMessage!!,
                    onDismiss = {
                        productViewModel.resetAddProductState()
                        navController.popBackStack() // Regresa a la lista
                    }
                )
            }
        }
    }
}