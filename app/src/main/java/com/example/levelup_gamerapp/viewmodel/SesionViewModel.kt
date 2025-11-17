package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.repository.RemoteUsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SesionViewModel : ViewModel() {

    private val repo = RemoteUsuariosRepository()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin

    private val _usuarioActual = MutableStateFlow<UsuarioDTO?>(null)
    val usuarioActual: StateFlow<UsuarioDTO?> = _usuarioActual

    /**
     * Intenta iniciar sesión contra el backend.
     * Devuelve true si las credenciales son válidas, false en caso contrario.
     */
    suspend fun login(correo: String, contrasena: String): Boolean {
        val usuario = repo.login(correo, contrasena)
        return if (usuario != null) {
            _usuarioActual.value = usuario
            _isLoggedIn.value = true
            _esAdmin.value = usuario.admin
            true
        } else {
            false
        }
    }

    /**
     * Cierra la sesión actual en la app (no hace llamada remota).
     */
    suspend fun logout() {
        _usuarioActual.value = null
        _isLoggedIn.value = false
        _esAdmin.value = false
    }
}
