package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CategoriaDTO

/**
 * Repositorio que se comunica con el backend para gestionar categorías. Este
 * repositorio ofrece métodos para obtener la lista completa de categorías y
 * crear nuevas categorías a través de la API REST. Cada métodoO lanza una
 * excepción si la operación no fue exitosa, permitiendo que el ViewModel
 * gestione los errores de red de manera adecuada.
 */
class RemoteCategoriasRepository {
    private val api = ApiClient.api

    /**
     * Obtiene todas las categorías disponibles en el servidor.
     *
     * @return Lista de [CategoriaDTO] representando las categorías actuales.
     */
    suspend fun obtenerCategorias(): List<CategoriaDTO> {
        return api.obtenerCategorias()
    }

    /**
     * Crea una nueva categoría en el backend.
     *
     * @param categoria La categoría que se desea crear.
     * @return La categoría creada devuelta por el servidor.
     * @throws Exception si la respuesta HTTP no indica éxito o no contiene
     *         cuerpo.
     */
    suspend fun crearCategoria(categoria: CategoriaDTO): CategoriaDTO {
        val response = api.crearCategoria(categoria)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta sin cuerpo al crear categoría")
        } else {
            throw Exception("Error al crear categoría: código ${response.code()}")
        }
    }
}