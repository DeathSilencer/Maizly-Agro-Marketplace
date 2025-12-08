package com.david.maizly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage       // <-- Lo quitamos
import coil.compose.SubcomposeAsyncImage // <-- ¡CAMBIO AQUÍ!
import com.david.maizly.ui.theme.MaizCrema
import com.david.maizly.ui.theme.MaizGrisClaro
import com.david.maizly.ui.theme.MaizVerdeOscuro
import com.david.maizly.ui.theme.TextoOscuro
import com.david.maizly.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerDetailScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    producerId: String
) {
    // 1. Carga el productor seleccionado
    LaunchedEffect(producerId) {
        viewModel.selectProducerById(producerId)
    }
    val producer by viewModel.selectedProducer.collectAsState()

    // 2. Carga TODOS los productos (para filtrarlos)
    val allProducts by viewModel.allProducts.collectAsState()

    // 3. Filtra los productos que coinciden con este productor
    val producerProducts = remember(allProducts, producerId) {
        allProducts.filter { it.producerId == producerId }
    }

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text(producer?.name ?: "Cargando...", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        producer?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- Encabezado del Productor ---
                // --- ¡CAMBIO AQUÍ! ---
                SubcomposeAsyncImage(
                    model = p.imageUrl,
                    contentDescription = p.name,
                    loading = { // Lo que se muestra mientras carga
                        SkeletonPlaceholder(modifier = Modifier.fillMaxSize())
                    },
                    // --- FIN DEL CAMBIO ---
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f), // Imagen panorámica
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = p.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = p.location,
                        fontSize = 16.sp,
                        color = TextoSecundario,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaizGrisClaro)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sobre Nosotros",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = p.description,
                        fontSize = 16.sp,
                        color = TextoSecundario,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Nuestros Productos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }

                // --- Cuadrícula de Productos ---
                // (Usamos un Box con altura fija para meter una Grid dentro de un Column scrollable)
                // Ajusta 280 según el alto de tu item
                val gridHeight = (producerProducts.size / 2 + producerProducts.size % 2) * 280
                Box(modifier = Modifier
                    .height(gridHeight.dp)
                    .padding(horizontal = 16.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        userScrollEnabled = false // Deshabilita el scroll de la grid
                    ) {
                        // Los 'ProductGridItem' ya tienen el skeleton
                        // gracias al cambio en ProductListScreen.kt
                        items(producerProducts) { product ->
                            ProductGridItem(
                                product = product,
                                onClick = {
                                    navController.navigate(Screen.ProductDetail.withId(product.id))
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espacio al final
            }
        } ?: run {
            // Muestra un spinner si el productor aún no se ha cargado
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaizVerdeOscuro)
            }
        }
    }
}