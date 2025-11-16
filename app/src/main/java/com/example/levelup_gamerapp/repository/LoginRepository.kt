package com.example.levelup_gamerapp.repository
import com.example.levelup_gamerapp.local.RegistroUsuarioDAO


// 🧩 Repositorio del Login
// Contiene la lógica de validación de usuario y contraseña contra la BD Room.
class LoginRepository(private val dao: RegistroUsuarioDAO) {

    // Verifica si el correo existe en la tabla usuario
    suspend fun existeCorreo(correo: String): Boolean {
        return dao.buscarPorCorreo(correo) != null
    }

    // Comprueba si la contraseña coincide con el correo ingresado
    suspend fun validarUsuario(correo: String, contrasena: String): Boolean {
        val usuario = dao.buscarPorCorreo(correo)
        return usuario?.contrasena == contrasena
    }
}