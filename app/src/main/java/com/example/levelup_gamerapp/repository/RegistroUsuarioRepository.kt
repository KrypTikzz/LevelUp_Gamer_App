package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.local.RegistroUsuarioDAO
import com.example.levelup_gamerapp.local.RegistroUsuarioEntity

class RegistroUsuarioRepository(private val dao: RegistroUsuarioDAO) {

    suspend fun registrarUsuario(usuario: RegistroUsuarioEntity) =
        dao.insertarUsuario(usuario)

    suspend fun verificarCorreo(correo: String) =
        dao.buscarPorCorreo(correo)

    suspend fun eliminarPorCorreo(correo: String) =
        dao.eliminarPorCorreo(correo)

    /**
     * Devuelve un flujo reactivo con todos los usuarios almacenados.
     */
    fun obtenerUsuarios() = dao.obtenerTodosUsuarios()

    /**
     * Devuelve un flujo reactivo con el usuario cuyo id coincida. Puede emitir null.
     */
    fun obtenerUsuarioPorId(id: Int) = dao.obtenerUsuarioPorId(id)

    /**
     * Actualiza los datos de un usuario existente.
     */
    suspend fun actualizarUsuario(usuario: RegistroUsuarioEntity) = dao.actualizarUsuario(usuario)

    /**
     * Elimina un usuario por su id.
     */
    suspend fun eliminarUsuario(id: Int) = dao.eliminarPorId(id)
}