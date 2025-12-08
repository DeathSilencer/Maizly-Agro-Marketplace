package com.david.maizly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.david.maizly.ui.theme.MaizCrema // <-- ¡IMPORTA TU COLOR!
import com.david.maizly.ui.theme.maizlyTheme
import androidx.navigation.compose.rememberNavController // <-- OJO: Puede que necesites este import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            maizlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaizCrema
                ) {
                    // Ya no llamamos a LoginScreen() directamente
                    AppNavigation()
                }
            }
        }
    }
}

