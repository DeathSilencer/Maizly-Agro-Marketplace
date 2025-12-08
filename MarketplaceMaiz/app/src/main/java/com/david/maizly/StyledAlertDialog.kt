package com.david.maizly

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.david.maizly.ui.theme.MaizAmarillo
import com.david.maizly.ui.theme.MaizCrema
import com.david.maizly.ui.theme.MaizVerdeOscuro
import com.david.maizly.ui.theme.TextoOscuro

/**
 * Nuestro diálogo de alerta personalizado que coincide con el tema de la app.
 *
 * @param title Título del diálogo (Ej: "Error" o "Éxito").
 * @param message El mensaje a mostrar al usuario.
 * @param onDismiss La acción a ejecutar cuando el usuario presiona "Entendido".
 */
@Composable
fun StyledAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        // --- Estilos de la App ---
        containerColor = MaizCrema,
        shape = RoundedCornerShape(12.dp),

        // --- Contenido ---
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaizVerdeOscuro
            )
        },
        text = {
            Text(
                text = message,
                color = TextoOscuro
            )
        },

        // --- Botón de Acción ---
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = MaizAmarillo),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Entendido", color = TextoOscuro)
            }
        }
    )
}