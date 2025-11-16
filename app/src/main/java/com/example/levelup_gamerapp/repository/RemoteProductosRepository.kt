package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.ProductoDTO

/**
 * Repositorio que consume el backend para obtener y gestionar productos.
 * Utiliza la interfaz [LevelUpApi] expuesta por [ApiClient] para realizar
 * operaciones de red. Cada métodoO lanza una excepción si la respuesta del
 * servidor no es exitosa, permitiendo que el ViewModel lo gestione a nivel
 * de interfaz.
 */
class RemoteProductosRepository {
    private val api = ApiClient.api

    /** Obtiene la lista de productos desde el servidor. */
    suspend fun obtenerProductos(): List<ProductoDTO> {
        return api.obtenerProductos()
    }

    /** Crea un nuevo producto en el backend y devuelve el producto resultante. */
    suspend fun crearProducto(producto: ProductoDTO): ProductoDTO {
        val response = api.crearProducto(producto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta sin cuerpo al crear producto")
        } else {
            throw Exception("Error al crear producto: código ${response.code()}")
        }
    }

    /** Elimina un producto existente por su id. */
    suspend fun eliminarProducto(id: Long) {
        val response = api.eliminarProducto(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar producto: código ${response.code()}")
        }
    }
}