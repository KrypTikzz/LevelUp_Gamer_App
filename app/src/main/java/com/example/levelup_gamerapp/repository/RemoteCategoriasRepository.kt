package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CategoriaDTO
import com.example.levelup_gamerapp.utils.ErrorUtils
import retrofit2.HttpException

/**
 * repositorio que se comunica con el backend para gestionar categorías.
 * todas las llamadas pasan por retrofit y se autentican vía jwt
 * usando el interceptor configurado en apiclient.
 */
open class RemoteCategoriasRepository {

    private val api = ApiClient.levelUpApi

    /**
     * obtiene todas las categorías disponibles en el servidor.
     */
    open suspend fun obtenerCategorias(): List<CategoriaDTO> {
        return try {
            api.obtenerCategorias()
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Categorías"))
        }
    }

    /**
     * crea una nueva categoría en el backend.
     */
    open suspend fun crearCategoria(categoria: CategoriaDTO): CategoriaDTO {
        val response = api.crearCategoria(categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta inválida del servidor (categoría sin cuerpo).")
        } else {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Categoría"))
        }
    }

    /**
     * actualiza una categoría existente en el backend.
     */
    open suspend fun actualizarCategoria(id: Long, categoria: CategoriaDTO): CategoriaDTO {
        val response = api.actualizarCategoria(id, categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta inválida del servidor (categoría sin cuerpo).")
        } else {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Categoría"))
        }
    }

    /**
     * elimina una categoría por su identificador.
     */
    open suspend fun eliminarCategoria(id: Long) {
        val response = api.eliminarCategoria(id)
        if (!response.isSuccessful) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Categoría"))
        }
    }
}
