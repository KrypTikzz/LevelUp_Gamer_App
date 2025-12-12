package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CategoriaDTO

/**
 * repositorio que se comunica con el backend para gestionar categorías.
 * todas las llamadas pasan por retrofit y se autentican vía jwt
 * usando el interceptor configurado en apiclient.
 */
open class RemoteCategoriasRepository {

    // api general del backend (no auth)
    private val api = ApiClient.levelUpApi

    /**
     * obtiene todas las categorías disponibles en el servidor.
     */
    open suspend fun obtenerCategorias(): List<CategoriaDTO> {
        return api.obtenerCategorias()
    }

    /**
     * crea una nueva categoría en el backend.
     */
    open suspend fun crearCategoria(categoria: CategoriaDTO): CategoriaDTO {
        val response = api.crearCategoria(categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("respuesta sin cuerpo al crear categoría")
        } else {
            throw Exception("error al crear categoría: código ${response.code()}")
        }
    }

    /**
     * actualiza una categoría existente en el backend.
     */
    open suspend fun actualizarCategoria(id: Long, categoria: CategoriaDTO): CategoriaDTO {
        val response = api.actualizarCategoria(id, categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("respuesta sin cuerpo al actualizar categoría")
        } else {
            throw Exception("error al actualizar categoría: código ${response.code()}")
        }
    }

    /**
     * elimina una categoría por su identificador.
     */
    open suspend fun eliminarCategoria(id: Long) {
        val response = api.eliminarCategoria(id)
        if (!response.isSuccessful) {
            throw Exception("error al eliminar categoría: código ${response.code()}")
        }
    }
}
