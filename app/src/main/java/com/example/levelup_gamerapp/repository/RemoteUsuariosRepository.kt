package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.UsuarioDTO

/**
 * Repositorio que se comunica con el backend para la gestión de usuarios.
 * Proporciona métodos para listar y crear usuarios mediante la API REST de
 * LevelUp Gamer. Al igual que el repositorio de productos, los métodos
 * lanzan excepciones cuando la respuesta HTTP no es satisfactoria.
 */
class RemoteUsuariosRepository {
    private val api = ApiClient.api

    /** Obtiene la lista de usuarios desde el servidor. */
    suspend fun obtenerUsuarios(): List<UsuarioDTO> {
        return api.obtenerUsuarios()
    }

    /** Registra un nuevo usuario y devuelve el usuario creado. */
    suspend fun crearUsuario(usuario: UsuarioDTO): UsuarioDTO {
        val response = api.crearUsuario(usuario)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta sin cuerpo al crear usuario")
        } else {
            throw Exception("Error al crear usuario: código ${response.code()}")
        }
    }
}