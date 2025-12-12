package com.example.levelup_gamerapp.core

/**
 * mantiene la sesión del usuario autenticado.
 * esta información viene directamente desde el backend (authresponse).
 */
object UserSession {

    var token: String? = null
    var idUsuario: Long? = null
    var nombreCompleto: String? = null
    var correo: String? = null
    var rol: String? = null

    fun estaAutenticado(): Boolean {
        return token != null
    }

    fun esAdmin(): Boolean {
        return rol == "ADMIN"
    }

    fun cerrarSesion() {
        token = null
        idUsuario = null
        nombreCompleto = null
        correo = null
        rol = null
    }
}
