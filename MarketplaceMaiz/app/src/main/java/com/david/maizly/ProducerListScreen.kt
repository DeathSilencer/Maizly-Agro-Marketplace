package com.david.maizly

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.david.maizly.ui.theme.MaizCrema
import com.david.maizly.ui.theme.MaizGrisClaro
import com.david.maizly.ui.theme.MaizVerdeOscuro
import com.david.maizly.ui.theme.TextoOscuro
import com.david.maizly.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducerListScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    val producers by productViewModel.allProducers.collectAsState()

    Scaffold(
        containerColor = MaizCrema,
        topBar = {
            TopAppBar(
                title = { Text("Productores", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaizVerdeOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(producers) { producer ->
                ProducerListItem(
                    producer = producer,
                    onClick = {
                        navController.navigate(Screen.ProducerDetail.withId(producer.id))
                    }
                )
            }
        }
    }
}

@Composable
fun ProducerListItem(producer: Producer, onClick: () -> Unit) {
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
                model = producer.imageUrl,
                contentDescription = producer.name,
                loading = { // Lo que se muestra mientras carga
                    SkeletonPlaceholder(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    )
                },
                // --- FIN DEL CAMBIO ---
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = producer.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = producer.location,
                    fontSize = 14.sp,
                    color = TextoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = producer.description,
                    fontSize = 12.sp,
                    color = TextoSecundario,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}