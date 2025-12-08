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
import androidx.compose.ui.draw.shadow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    productId: String
) {
    // 1. Carga el producto que vamos a editar
    LaunchedEffect(productId) {
        productViewModel.selectProductById(productId)
    }
    val productToEdit by productViewModel.selectedProduct.collectAsState()

    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Nueva imagen
    var originalImageUrl by remember { mutableStateOf<String?>(null) } // Imagen original

    // Estado del ViewModel
    val state by productViewModel.productUpdateState.collectAsState()

    // 2. Llena los campos cuando el producto se cargue
    LaunchedEffect(productToEdit) {
        productToEdit?.let {
            nombre = it.nombre
            precio = it.precio.toString()
            descripcion = it.descripcion
            stock = it.stock.toString()
            originalImageUrl = it.imageUrl // Guarda la URL original
        }
    }

    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri // Guarda la *nueva* URI
    }

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        // Muestra un spinner si el producto no ha cargado
        if (productToEdit == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaizVerdeOscuro)
            }
        } else {
            // Muestra el formulario cuando el producto ya cargó
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
                        // Muestra la nueva imagen si se seleccionó, si no, la original
                        AsyncImage(
                            model = imageUri ?: originalImageUrl,
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Cambiar Imagen", color = MaizVerdeOscuro, fontWeight = FontWeight.Bold)
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

                        // --- Campo Stock ---
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

                    // --- Botón de Guardar Cambios ---
                    Button(
                        onClick = {
                            productToEdit?.let {
                                productViewModel.updateProduct(
                                    productToUpdate = it,
                                    nombre = nombre,
                                    precioStr = precio,
                                    descripcion = descripcion,
                                    stockStr = stock,
                                    imageUri = imageUri // Pasa la nueva URI (o null)
                                )
                            }
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
                            text = "Guardar Cambios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    }
                }

                // --- Spinner de Carga ---
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaizCrema.copy(alpha = 0.8f))
                            .clickable(enabled = false, onClick = {}),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaizVerdeOscuro)
                    }
                }

                // --- Diálogo de Error ---
                if (state.error != null) {
                    StyledAlertDialog(
                        title = "Error al Actualizar",
                        message = state.error!!,
                        onDismiss = { productViewModel.resetProductUpdateState() }
                    )
                }

                // --- Diálogo de Éxito ---
                if (state.updateSuccess) {
                    StyledAlertDialog(
                        title = "¡Éxito!",
                        message = "Tu producto se ha actualizado correctamente.",
                        onDismiss = {
                            productViewModel.resetProductUpdateState()
                            navController.popBackStack() // Regresa a la pantalla de detalle
                        }
                    )
                }
            }
        }
    }
}