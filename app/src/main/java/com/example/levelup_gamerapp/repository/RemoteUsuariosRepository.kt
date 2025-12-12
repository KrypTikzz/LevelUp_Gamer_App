package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.UsuarioDTO
import retrofit2.Response
import retrofit2.HttpException

/**
 * repositorio remoto para la gestión de usuarios.
 * todas las operaciones se realizan contra el backend
 * y se autentican mediante jwt (interceptor).
 */
open class RemoteUsuariosRepository {

    // api general del backend (usuarios, productos, etc.)
    private val api = ApiClient.levelUpApi

    /** obtiene todos los usuarios almacenados en el backend. */
    open suspend fun obtenerUsuarios(): List<UsuarioDTO> {
        return api.obtenerUsuarios()
    }

    /** obtiene un usuario por su identificador. */
    open suspend fun obtenerUsuario(id: Long): UsuarioDTO {
        return api.obtenerUsuario(id)
    }

    /** crea un nuevo usuario. devuelve el usuario resultante. */
    open suspend fun crearUsuario(usuario: UsuarioDTO): UsuarioDTO {
        val response: Response<UsuarioDTO> = api.crearUsuario(usuario)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("respuesta sin cuerpo al crear usuario")
        } else {
            throw Exception("error al crear usuario: código ${response.code()}")
        }
    }

    /** actualiza un usuario existente. devuelve el usuario actualizado. */
    open suspend fun actualizarUsuario(id: Long, usuario: UsuarioDTO): UsuarioDTO {
        val response: Response<UsuarioDTO> = api.actualizarUsuario(id, usuario)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("respuesta sin cuerpo al actualizar usuario")
        } else {
            throw Exception("error al actualizar usuario: código ${response.code()}")
        }
    }

    /** elimina un usuario por su identificador. */
    open suspend fun eliminarUsuario(id: Long) {
        val response: Response<Unit> = api.eliminarUsuario(id)
        if (!response.isSuccessful) {
            throw Exception("error al eliminar usuario: código ${response.code()}")
        }
    }

    /** busca un usuario por correo. devuelve null si no existe. */
    open suspend fun buscarPorCorreo(correo: String): UsuarioDTO? {
        return try {
            api.buscarUsuarioPorCorreo(correo)
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    /**
     * login NO DEBE USARSE aquí
     * el login se realiza exclusivamente vía AuthApi (/api/auth/login)
     * este metodo se deja solo si el backend antiguo aún lo expone,
     * pero NO debe usarse en la app final.
     */
    @Deprecated(
        message = "usar AuthApi para login con jwt",
        level = DeprecationLevel.WARNING
    )
    open suspend fun login(correo: String, contrasena: String): UsuarioDTO? {
        return api.login(correo, contrasena)
    }
}
