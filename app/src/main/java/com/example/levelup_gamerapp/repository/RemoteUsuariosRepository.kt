package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.utils.ErrorUtils
import retrofit2.HttpException
import retrofit2.Response

/**
 * repositorio remoto para la gestión de usuarios.
 * todas las operaciones se realizan contra el backend
 * y se autentican mediante jwt (interceptor).
 */
open class RemoteUsuariosRepository {

    private val api = ApiClient.levelUpApi

    /** obtiene todos los usuarios almacenados en el backend. */
    open suspend fun obtenerUsuarios(): List<UsuarioDTO> {
        return try {
            api.obtenerUsuarios()
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Usuarios"))
        }
    }

    /** obtiene un usuario por su identificador. */
    open suspend fun obtenerUsuario(id: Long): UsuarioDTO {
        return try {
            api.obtenerUsuario(id)
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Usuario"))
        }
    }

    /** crea un nuevo usuario. devuelve el usuario resultante. */
    open suspend fun crearUsuario(usuario: UsuarioDTO): UsuarioDTO {
        val response: Response<UsuarioDTO> = api.crearUsuario(usuario)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta inválida del servidor (usuario sin cuerpo).")
        } else {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Usuario"))
        }
    }

    /** actualiza un usuario existente. devuelve el usuario actualizado. */
    open suspend fun actualizarUsuario(id: Long, usuario: UsuarioDTO): UsuarioDTO {
        val response: Response<UsuarioDTO> = api.actualizarUsuario(id, usuario)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta inválida del servidor (usuario sin cuerpo).")
        } else {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Usuario"))
        }
    }

    /** elimina un usuario por su identificador. */
    open suspend fun eliminarUsuario(id: Long) {
        val response: Response<Unit> = api.eliminarUsuario(id)
        if (!response.isSuccessful) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Usuario"))
        }
    }

    /** busca un usuario por correo. devuelve null si no existe. */
    open suspend fun buscarPorCorreo(correo: String): UsuarioDTO? {
        return try {
            api.buscarUsuarioPorCorreo(correo)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                null
            } else {
                throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Usuario"))
            }
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
