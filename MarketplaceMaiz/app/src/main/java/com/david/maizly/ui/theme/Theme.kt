package com.david.maizly.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores CLARO (Modo Día) usando nuestra paleta Maíz
private val LightColorScheme = lightColorScheme(
    primary = MaizVerdeOscuro,
    secondary = MaizAmarillo,
    background = MaizCrema, // El fondo de la app
    surface = MaizCrema, // El fondo de tarjetas, etc.
    onPrimary = Color.White, // Texto sobre el color primario
    onSecondary = TextoOscuro, // Texto sobre el color secundario
    onBackground = TextoOscuro, // Texto sobre el fondo
    onSurface = TextoOscuro // Texto sobre las superficies
)

// Esquema de colores OSCURO (Modo Noche)
private val DarkColorScheme = darkColorScheme(
    primary = MaizAmarillo, // El amarillo resalta bien en modo noche
    secondary = MaizVerdeOscuro,
    background = Color(0xFF1C1B1F), // Un fondo oscuro estándar
    surface = Color(0xFF1C1B1F),
    onPrimary = TextoOscuro,
    onSecondary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun maizlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Android 12+) lo desactivamos para forzar nuestros colores
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Hacemos que la barra de estado sea del color de fondo (Crema)
            window.statusBarColor = colorScheme.background.toArgb()

            // Le decimos al sistema que los iconos de la barra (reloj, batería)
            // deben ser oscuros, porque nuestro fondo es claro.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Esto usa el archivo Typography.kt
        content = content
    )
}