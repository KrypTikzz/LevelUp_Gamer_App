package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.levelup_gamerapp.core.UserSession
import com.example.levelup_gamerapp.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Sesión REAL con JWT:
 * - Login: /api/auth/login
 * - Registro: /api/auth/registro
 *
 * NO usar /api/usuarios/login (endpoint viejo, bloqueado por SecurityConfig).
 */
class SesionViewModel : ViewModel() {

    private val loginRepository = LoginRepository()

    private val _isLoggedIn = MutableStateFlow(UserSession.estaAutenticado())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _esAdmin = MutableStateFlow(UserSession.esAdmin())
    val esAdmin: StateFlow<Boolean> = _esAdmin

    fun syncFromUserSession() {
        _isLoggedIn.value = UserSession.estaAutenticado()
        _esAdmin.value = UserSession.esAdmin()
    }

    suspend fun login(correo: String, contrasena: String): Boolean {
        loginRepository.login(correo, contrasena) // guarda token + datos en UserSession
        syncFromUserSession()
        return true
    }

    fun logout() {
        UserSession.cerrarSesion()
        syncFromUserSession()
    }
}
