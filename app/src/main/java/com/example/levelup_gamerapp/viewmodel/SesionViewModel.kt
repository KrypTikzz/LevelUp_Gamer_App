package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SesionViewModel : ViewModel() {

    /**
     * Indica si existe una sesión de usuario activa. Se expone como [StateFlow] para
     * que las composables puedan reaccionar automáticamente a cambios.
     */
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    /**
     * Correo electrónico del usuario actualmente autenticado. Si no hay sesión,
     * será null. Esta información puede utilizarse para mostrar datos personalizados.
     */
    private val _correoUsuario = MutableStateFlow<String?>(null)
    val correoUsuario: StateFlow<String?> = _correoUsuario

    /**
     * Flag que indica si el usuario autenticado posee privilegios de administrador.
     * La determinación de administrador se hace al momento de iniciar sesión.
     */
    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin

    /**
     * Marca la sesión como iniciada. Se puede indicar si el usuario tiene rol
     * administrador para habilitar funcionalidades adicionales en la UI.
     *
     * @param correo Correo electrónico del usuario.
     * @param esAdmin Indica si el usuario tiene permisos de administrador.
     */
    fun login(correo: String, esAdmin: Boolean = false) {
        viewModelScope.launch {
            _isLoggedIn.value = true
            _correoUsuario.value = correo
            _esAdmin.value = esAdmin
        }
    }

    /**
     * Cierra la sesión eliminando cualquier información asociada al usuario
     * autenticado.
     */
    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _correoUsuario.value = null
            _esAdmin.value = false
        }
    }
}
