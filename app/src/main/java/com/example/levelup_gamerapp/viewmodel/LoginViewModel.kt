package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 🧠 ViewModel del Login
// Maneja la lógica de validación y los mensajes que se muestran en pantalla.
class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    /**
     * Mensaje informativo que se mostrará en la pantalla de login. Puede ser
     * un error de validación, un mensaje de éxito u otra retroalimentación.
     */
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    /**
     * Indica si el inicio de sesión fue exitoso. Cuando cambia a true, la
     * pantalla debería navegar a la siguiente vista y restablecer este valor.
     */
    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    /**
     * Indica si el usuario autenticado posee permisos de administrador. Esto
     * permite a la UI mostrar u ocultar funcionalidades administrativas.
     */
    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin

    /**
     * Valida las credenciales proporcionadas. Actualiza los estados de
     * [mensaje], [loginExitoso] y [esAdmin] según corresponda.
     *
     * @param correo correo del usuario
     * @param contrasena contraseña ingresada
     */
    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            // Validaciones básicas
            if (correo.isBlank() || contrasena.isBlank()) {
                _mensaje.value = "Completa todos los campos"
                _loginExitoso.value = false
                _esAdmin.value = false
                return@launch
            }

            // Verifica si existe el correo
            if (!repository.existeCorreo(correo)) {
                _mensaje.value = "Usuario no encontrado"
                _loginExitoso.value = false
                _esAdmin.value = false
                return@launch
            }

            // Valida contraseña
            val valido = repository.validarUsuario(correo, contrasena)
            if (valido) {
                _mensaje.value = "Inicio de sesión exitoso 🎮"
                _loginExitoso.value = true
                // Determina si es administrador comparando el correo con una cuenta predefinida
                _esAdmin.value = correo.equals("admin@levelupgamer.cl", ignoreCase = true)
            } else {
                _mensaje.value = "Contraseña incorrecta"
                _loginExitoso.value = false
                _esAdmin.value = false
            }
        }
    }

    /**
     * Restablece los estados [loginExitoso] y [mensaje]. Llamar después de
     * reaccionar a un inicio de sesión exitoso para evitar navegaciones
     * redundantes.
     */
    fun resetEstado() {
        _loginExitoso.value = false
        _mensaje.value = ""
    }

    /**
     * Limpia únicamente el mensaje. Útil cuando el usuario modifica los
     * campos y se desea ocultar el mensaje anterior.
     */
    fun limpiarMensaje() {
        _mensaje.value = ""
    }
}
