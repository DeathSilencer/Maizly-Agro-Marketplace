package com.david.maizly

import androidx.compose.foundation.background
import androidx.compose.foundation.border // <-- Asegúrate de que este import esté
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
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
import coil.compose.SubcomposeAsyncImage
import com.david.maizly.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel, // <-- ¡AÑADIDO!
    productId: String
) {
    // Busca el producto seleccionado CADA VEZ que se abre la pantalla
    LaunchedEffect(Unit) {
        viewModel.selectProductById(productId)
    }

    val product by viewModel.selectedProduct.collectAsState()
    // --- ¡NUEVO! Limpieza al salir ---
// Esto asegura que si sales, el producto se limpia.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetSelectedProduct() // Limpia el producto al salir
        }
    }
    val cartState by cartViewModel.uiState.collectAsState()

    // --- ¡CAMBIO! ---
    // Ya no necesitamos 'selectedQuantity' aquí.
    // Creamos un estado para controlar la visibilidad del *nuevo* diálogo.
    var showQuantityDialog by remember { mutableStateOf(false) }
    // --- FIN DEL CAMBIO ---
    val updateState by viewModel.productUpdateState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

// Obtenemos el ID del usuario actual
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
// --- FIN NUEVO ---

    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    // --- ¡NUEVO! ---
// Efecto para manejar el éxito o error de la eliminación
    LaunchedEffect(key1 = updateState) {
        if (updateState.deleteSuccess) {
            viewModel.resetProductUpdateState() // Resetea el estado
            navController.popBackStack() // Regresa a la lista
        }
        // (Podríamos añadir un diálogo de error si updateState.error no es nulo)
    }
// --- FIN NUEVO ---

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text(product?.nombre ?: "Cargando...", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                // --- ¡NUEVO! ---
                actions = {
                    // Comprobamos si el producto no es nulo y si el ID del productor es "p_user"
                    val isOwner = product?.producerId == "p_user"

                    if (isOwner) {
                        // --- ¡NUEVO! Botón de Editar ---
                        IconButton(onClick = {
                            navController.navigate(Screen.EditProduct.withId(product!!.id))
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                        // --- FIN NUEVO ---

                        // Botón de eliminar
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                        }
                    }
                }
                // --- FIN NUEVO ---
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // --- ¡CAMBIO! ---
                    // Se eliminó la fila del selector de cantidad de aquí.
                    // --- FIN DEL CAMBIO ---

                    // --- Botón de Añadir al Carrito ---
                    Button(
                        onClick = {
                            // --- ¡CAMBIO! ---
                            // En lugar de añadir, ahora ABRE el diálogo.
                            showQuantityDialog = true
                            // --- FIN DEL CAMBIO ---
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                        enabled = product != null && product!!.stock > 0
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (product != null && product!!.stock > 0) "Añadir al carrito" else "Producto Agotado",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    }
                }
            }
        }
    ) { padding ->
        // Contenido de la pantalla
        product?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                SubcomposeAsyncImage(
                    model = p.imageUrl,
                    contentDescription = p.nombre,
                    loading = {
                        SkeletonPlaceholder(modifier = Modifier.fillMaxSize())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = p.nombre,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatter.format(p.precio),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaizVerdeOscuro
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val stockText = if (p.stock > 0) "Disponible: ${p.stock} unidades" else "Agotado"
                    val stockColor = if (p.stock > 0) TextoSecundario else Color.Red.copy(alpha = 0.8f)
                    Text(
                        text = stockText,
                        fontSize = 16.sp,
                        color = stockColor,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Vendido por: ${p.producerName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoOscuro,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate(Screen.ProducerDetail.withId(p.producerId))
                            }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaizGrisClaro)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Descripción",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = p.descripcion.ifBlank { "No hay descripción disponible." },
                        fontSize = 16.sp,
                        color = TextoSecundario,
                        lineHeight = 24.sp
                    )
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaizVerdeOscuro)
            }
        }

        // --- ¡NUEVA LÓGICA DE DIÁLOGOS! ---

        // 1. Muestra el diálogo de SELECCIÓN DE CANTIDAD
        if (showQuantityDialog && product != null) {
            QuantityDialog(
                product = product!!,
                onDismiss = { showQuantityDialog = false },
                onConfirm = { quantity ->
                    // Cuando el usuario confirma, AHORA SÍ llamamos al ViewModel
                    cartViewModel.addMultipleToCart(product!!, quantity)
                    showQuantityDialog = false
                }
            )
        }

        // 2. Muestra el diálogo de ÉXITO (si el VM lo indica)
        if (cartState.successMessage != null) {
            StyledAlertDialog(
                title = "¡Éxito!",
                message = cartState.successMessage!!,
                onDismiss = { cartViewModel.resetCartMessages() }
            )
        }

        // 3. Muestra el diálogo de ERROR (si el VM lo indica)
        if (cartState.error != null) {
            StyledAlertDialog(
                title = "Stock Insuficiente",
                message = cartState.error!!,
                onDismiss = { cartViewModel.resetCartMessages() }
            )
        }
        // --- FIN DE LÓGICA DE DIÁLOGOS ---
    }

    // --- ¡NUEVOS DIÁLOGOS Y SPINNER! ---

// 1. Diálogo de confirmación de borrado
    if (showDeleteDialog && product != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaizCrema,
            shape = RoundedCornerShape(12.dp),
            title = {
                Text(
                    text = "Eliminar Producto",
                    fontWeight = FontWeight.Bold,
                    color = MaizVerdeOscuro
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres eliminar \"${product!!.nombre}\"? Esta acción no se puede deshacer.",
                    color = TextoOscuro
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar", color = MaizVerdeOscuro)
                }
            }
        )
    }

// 2. Spinner de carga mientras se elimina
    if (updateState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaizCrema.copy(alpha = 0.8f))
                .clickable(enabled = false, onClick = {}), // Bloquea clics
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaizVerdeOscuro)
        }
    }
// --- FIN DE NUEVOS DIÁLOGOS Y SPINNER ---
}