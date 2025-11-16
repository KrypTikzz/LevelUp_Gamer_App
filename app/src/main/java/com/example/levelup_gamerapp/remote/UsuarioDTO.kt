package com.example.levelup_gamerapp.remote

/**
 * Representación de un usuario en el backend. Este DTO refleja la entidad
 * Usuario expuesta por las rutas REST de Spring. Se utiliza para listar y
 * crear usuarios desde la app móvil.
 */
data class UsuarioDTO(
    val id: Long? = null,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val edad: Int,
    val admin: Boolean = false
)