package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.UsuarioDTO
import retrofit2.Response
import retrofit2.HttpException

/**
 * Repositorio remoto para la gestión de usuarios.
 * Ofrece operaciones CRUD completas contra las rutas expuestas en el backend.
 */
class RemoteUsuariosRepository {
    private val api = ApiClient.api

    /** Obtiene todos los usuarios almacenados en el backend. */
    suspend fun obtenerUsuarios(): List<UsuarioDTO> {
        return api.obtenerUsuarios()
    }

    /** Obtiene un usuario por su identificador. */
    suspend fun obtenerUsuario(id: Long): UsuarioDTO {
        return api.obtenerUsuario(id)
    }

    /** Crea un nuevo usuario. Devuelve el usuario resultante. */
    suspend fun crearUsuario(usuario: UsuarioDTO): UsuarioDTO {
        val response: Response<UsuarioDTO> = api.crearUsuario(usuario)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta sin cuerpo al crear usuario")
        } else {
            throw Exception("Error al crear usuario: código ${'$'}{response.code()}")
        }
    }

    /** Actualiza un usuario existente. Devuelve el usuario actualizado. */
    suspend fun actualizarUsuario(id: Long, usuario: UsuarioDTO): UsuarioDTO {
        val response: Response<UsuarioDTO> = api.actualizarUsuario(id, usuario)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta sin cuerpo al actualizar usuario")
        } else {
            throw Exception("Error al actualizar usuario: código ${'$'}{response.code()}")
        }
    }

    /** Elimina un usuario por su identificador. */
    suspend fun eliminarUsuario(id: Long) {
        val response: Response<Unit> = api.eliminarUsuario(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar usuario: código ${'$'}{response.code()}")
        }
    }

    /** Busca un usuario por su correo electrónico. Devuelve null si no se encuentra. */
    suspend fun buscarPorCorreo(correo: String): UsuarioDTO? {
        return try {
            api.buscarUsuarioPorCorreo(correo)
        } catch (e: HttpException) {
            // Si el backend responde 404 (no encontrado), lo interpretamos como "no existe usuario"
            if (e.code() == 404) {
                null
            } else {
                // Otros códigos (500, 400, etc.) los volvemos a lanzar para no ocultar errores graves
                throw e
            }
        }
    }

    /** Realiza el inicio de sesión. Devuelve el usuario autenticado o null si las credenciales no son válidas. */
    suspend fun login(correo: String, contrasena: String): UsuarioDTO? {
        return api.login(correo, contrasena)
    }
}