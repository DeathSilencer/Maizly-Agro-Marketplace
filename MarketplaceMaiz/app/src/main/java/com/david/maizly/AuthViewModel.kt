package com.david.maizly

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // <-- ¡AÑADIDO!
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Estado de la UI para saber qué está pasando
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val isUpdating: Boolean = false // <-- AÑADIDO
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow() // <-- ¡ARREGLO AQUÍ!

    // --- ¡NUEVA FUNCIÓN PARA TRADUCIR ERRORES! ---
    private fun translateFirebaseError(exception: Exception): String {
        Log.w("AuthViewModel", "Error de Firebase: ", exception)
        return when ((exception as? FirebaseAuthException)?.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El formato del correo electrónico no es válido."
            "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil. Debe tener al menos 6 caracteres."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo electrónico ya está registrado."
            "ERROR_USER_NOT_FOUND", "ERROR_INVALID_CREDENTIAL" -> "Correo o contraseña incorrectos."
            "ERROR_WRONG_PASSWORD" -> "Correo o contraseña incorrectos."
            else -> "Ocurrió un error inesperado. Inténtalo de nuevo."
        }
    }
    // --- FIN DE TRADUCCIÓN ---

    fun registerUser(nombre: String, email: String, pass: String, confirmPass: String) {
        if (nombre.isBlank() || email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.value = AuthState(error = "Todos los campos son obligatorios.")
            return
        }
        if (pass != confirmPass) {
            _uiState.value = AuthState(error = "Las contraseñas no coinciden.")
            return
        }

        _uiState.value = AuthState(isLoading = true)
        viewModelScope.launch {
            try {
                // 1. Crear el usuario
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                if (result.user != null) {
                    // 2. Añadir el nombre al perfil
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()
                    result.user!!.updateProfile(profileUpdates).await()

                    Log.i("AuthViewModel", "Usuario registrado con éxito: ${result.user!!.uid}")
                    _uiState.value = AuthState(isSuccess = true, successMessage = "¡Registro exitoso! Ahora puedes iniciar sesión.")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState(error = translateFirebaseError(e))
            }
        }
    }

    fun loginUser(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthState(error = "Todos los campos son obligatorios.")
            return
        }
        _uiState.value = AuthState(isLoading = true)
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _uiState.value = AuthState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AuthState(error = translateFirebaseError(e))
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun resetState() {
        _uiState.value = AuthState()
    }

    // --- ¡NUEVA FUNCIÓN PARA ACTUALIZAR FOTO (SIMULADA)! ---
    fun updateProfilePicture(uri: Uri) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = _uiState.value.copy(error = "Usuario no encontrado.")
            return
        }

        _uiState.value = _uiState.value.copy(isUpdating = true) // Inicia la carga

        viewModelScope.launch {
            try {
                // 1. No subimos a Storage. Usamos la URI local directamente.
                // Firebase Auth guardará esta URI (content://...) en el perfil del usuario.
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri) // <-- ¡El cambio clave!
                    .build()

                // 2. Actualiza el perfil de Firebase Auth
                user.updateProfile(profileUpdates).await()

                Log.i("AuthViewModel", "Foto de perfil (local) actualizada con éxito.")
                // Resetea el estado (esto forzará a la UI a recargar el 'currentUser.photoUrl')
                _uiState.value = _uiState.value.copy(isUpdating = false, successMessage = "Foto de perfil actualizada.")

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al actualizar foto: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isUpdating = false, error = "Error al actualizar la foto: ${e.message}")
            }
        }
    }
    // --- ¡NUEVA FUNCIÓN PARA ACTUALIZAR NOMBRE! ---
    fun updateDisplayName(newName: String) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = _uiState.value.copy(error = "Usuario no encontrado.")
            return
        }

        if (newName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "El nombre no puede estar vacío.")
            return
        }

        _uiState.value = _uiState.value.copy(isUpdating = true) // Inicia la carga

        viewModelScope.launch {
            try {
                // 1. Crea la solicitud de actualización
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()

                // 2. Actualiza el perfil de Firebase Auth
                user.updateProfile(profileUpdates).await()

                Log.i("AuthViewModel", "Nombre actualizado con éxito.")
                // Resetea el estado y avisa que fue exitoso
                _uiState.value = _uiState.value.copy(isUpdating = false, successMessage = "Nombre actualizado correctamente.")

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al actualizar nombre: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isUpdating = false, error = "Error al actualizar el nombre.")
            }
        }
    }
}