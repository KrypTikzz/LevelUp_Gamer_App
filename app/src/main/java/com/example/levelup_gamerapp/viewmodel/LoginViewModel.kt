package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.core.UserSession
import com.example.levelup_gamerapp.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(correo, contrasena)

                // guardar sesión (backend manda la verdad)
                UserSession.token = response.token
                UserSession.idUsuario = response.idUsuario
                UserSession.nombreCompleto = response.nombreCompleto
                UserSession.correo = response.correo
                UserSession.rol = response.rol

                _loginExitoso.value = true
                _mensaje.value = "Bienvenido ${response.nombreCompleto}"

            } catch (e: Exception) {
                _mensaje.value = "Credenciales incorrectas"
                _loginExitoso.value = false
            }
        }
    }

    fun resetEstado() {
        _loginExitoso.value = false
        _mensaje.value = ""
    }
}
