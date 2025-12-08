package com.david.maizly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.david.maizly.ui.theme.MaizCrema
import com.david.maizly.ui.theme.MaizVerdeOscuro
import com.david.maizly.ui.theme.TextoSecundario

/**
 * Nueva pantalla que muestra solo los productos publicados por el usuario actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPublicationsScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    // Obtenemos la lista de productos filtrada del ViewModel
    val userProducts by productViewModel.userProducts.collectAsState()

    // Reutilizamos la lógica del modo de vista (¡útil aquí también!)
    val viewMode by productViewModel.viewMode.collectAsState()

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    // Botón para cambiar vista
                    IconButton(onClick = { productViewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.ViewList,
                            contentDescription = "Cambiar Vista",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (userProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no has publicado ningún producto.",
                    fontSize = 18.sp,
                    color = TextoSecundario,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Reutilizamos la misma lógica de lista/cuadrícula
            when (viewMode) {
                ViewMode.LIST -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(userProducts) { product ->
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(userProducts) { product ->
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