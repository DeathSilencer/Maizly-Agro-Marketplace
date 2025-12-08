package com.david.maizly

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // <-- ¡AQUÍ ESTÁ EL ARREGLO!
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

/**
 * Un Composable que muestra un placeholder gris simple.
 * Este es el fondo sobre el cual se moverá el brillo.
 */
@Composable
fun SkeletonPlaceholder(modifier: Modifier) {
    Box(
        modifier = modifier
            .shimmerEffect() // <-- Aplicamos el efecto de brillo
            .background(Color.Gray.copy(alpha = 0.3f))
    )
}

/**
 * Modificador personalizado que aplica el efecto de "shimmer" (brillo).
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size = IntSize.Zero
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")

    // Anima un valor flotante de 0f a 1f
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000) // Duración de la animación: 1 segundo
        ),
        label = "ShimmerOffsetX"
    )

    // Colores del gradiente de brillo
    val shimmerColors = listOf(
        Color.Gray.copy(alpha = 0.3f),
        Color.Gray.copy(alpha = 0.1f),
        Color.Gray.copy(alpha = 0.3f),
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size // Obtenemos el tamaño del Composable
        }
}