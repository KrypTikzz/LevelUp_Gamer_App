package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CategoriaDTO

/**
 * Repositorio que se comunica con el backend para gestionar categorías. Este
 * repositorio ofrece métodos para obtener la lista completa de categorías,
 * crear nuevas, actualizarlas y eliminarlas a través de la API REST.
 */
open class RemoteCategoriasRepository {
    private val api = ApiClient.api

    /**
     * Obtiene todas las categorías disponibles en el servidor.
     *
     * @return Lista de [CategoriaDTO] representando las categorías actuales.
     */
    open suspend fun obtenerCategorias(): List<CategoriaDTO> {
        return api.obtenerCategorias()
    }

    /**
     * Crea una nueva categoría en el backend.
     */
    open suspend fun crearCategoria(categoria: CategoriaDTO): CategoriaDTO {
        val response = api.crearCategoria(categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta sin cuerpo al crear categoría")
        } else {
            throw Exception("Error al crear categoría: código ${response.code()}")
        }
    }

    /**
     * Actualiza una categoría existente en el backend.
     */
    open suspend fun actualizarCategoria(id: Long, categoria: CategoriaDTO): CategoriaDTO {
        val response = api.actualizarCategoria(id, categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta sin cuerpo al actualizar categoría")
        } else {
            throw Exception("Error al actualizar categoría: código ${response.code()}")
        }
    }

    /**
     * Elimina una categoría por su identificador.
     */
    open suspend fun eliminarCategoria(id: Long) {
        val response = api.eliminarCategoria(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar categoría: código ${response.code()}")
        }
    }
}
