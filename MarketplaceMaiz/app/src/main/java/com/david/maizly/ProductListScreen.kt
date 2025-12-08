package com.david.maizly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage       // <-- Lo quitamos
import coil.compose.SubcomposeAsyncImage // <-- ¡CAMBIO AQUÍ!
import com.david.maizly.ui.theme.*
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val products by productViewModel.products.collectAsState()
    val searchQuery by productViewModel.searchQuery.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()
    val viewMode by productViewModel.viewMode.collectAsState()

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Maíz Libre", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                actions = {
                    // Botón para cambiar vista
                    IconButton(onClick = { productViewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = "Cambiar Vista",
                            tint = Color.White
                        )
                    }

                    // Botón del Carrito
                    BadgedBox(
                        badge = {
                            if (cartState.items.isNotEmpty()) {
                                Badge(
                                    containerColor = MaizAmarillo,
                                    contentColor = TextoOscuro
                                ) {
                                    Text("${cartState.items.sumOf { it.quantity }}")
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color.White)
                        }
                    }

                    // --- ¡NUEVO BOTÓN! ---
                    IconButton(onClick = { navController.navigate(Screen.ProducerList.route) }) {
                        Icon(Icons.Default.Groups, contentDescription = "Productores", tint = Color.White)
                    }
                    // --- FIN NUEVO BOTÓN ---

                    // Botón de Perfil
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddProduct.route) },
                containerColor = MaizAmarillo,
                contentColor = TextoOscuro,
                shape = CircleShape,
                modifier = Modifier.shadow(elevation = 8.dp, shape = CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // --- BARRA DE BÚSQUEDA ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { productViewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar producto...", color = TextoSecundario) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaizVerdeOscuro)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { productViewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = MaizVerdeOscuro)
                        }
                    }
                },
                shape = RoundedCornerShape(50), // Forma de píldora
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = MaizGrisClaro,
                    focusedIndicatorColor = MaizVerdeOscuro,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro
                ),
                singleLine = true
            )

            // --- LISTA DE PRODUCTOS ---
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No se encontraron resultados" else "No hay productos publicados",
                        fontSize = 18.sp,
                        color = TextoSecundario
                    )
                }
            } else {
                when (viewMode) {
                    ViewMode.LIST -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(products) { product ->
                                ProductListItem(
                                    product = product,
                                    onClick = {
                                        navController.navigate(Screen.ProductDetail.withId(product.id))
                                    }
                                )
                            }
                        }
                    }
                    ViewMode.GRID -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(products) { product ->
                                ProductGridItem(
                                    product = product,
                                    onClick = {
                                        navController.navigate(Screen.ProductDetail.withId(product.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onClick: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    val stockText = if (product.stock > 0) "Disponible: ${product.stock}" else "Agotado"
    val stockColor = if (product.stock > 0) MaizVerdeOscuro else Color.Red.copy(alpha = 0.7f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                model = product.imageUrl,
                contentDescription = product.nombre,
                loading = { // Lo que se muestra mientras carga
                    SkeletonPlaceholder(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    )
                },
                // --- FIN DEL CAMBIO ---
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = product.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatter.format(product.precio),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vendido por: ${product.producerName}",
                    fontSize = 12.sp,
                    color = TextoSecundario,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stockText,
                    fontSize = 12.sp,
                    color = stockColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProductGridItem(product: Product, onClick: () -> Unit) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    val stockText = if (product.stock > 0) "Disponible: ${product.stock}" else "Agotado"
    val stockColor = if (product.stock > 0) MaizVerdeOscuro else Color.Red.copy(alpha = 0.7f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // --- ¡CAMBIO AQUÍ! ---
            SubcomposeAsyncImage(
                model = product.imageUrl,
                contentDescription = product.nombre,
                loading = { // Lo que se muestra mientras carga
                    SkeletonPlaceholder(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    )
                },
                // --- FIN DEL CAMBIO ---
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Imagen cuadrada
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatter.format(product.precio),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stockText,
                    fontSize = 12.sp,
                    color = stockColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}