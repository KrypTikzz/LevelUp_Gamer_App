package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.ProductoDTO
import retrofit2.Response

/**
 * Repositorio remoto para gestionar productos a través del backend.
 * Se apoya en [LevelUpApi] proporcionado por [ApiClient] para realizar las
 * peticiones HTTP.
 */
class RemoteProductosRepository {
    private val api = ApiClient.api

    /**
     * Obtiene el listado completo de productos desde el servidor.
     */
    suspend fun obtenerProductos(): List<ProductoDTO> {
        return api.obtenerProductos()
    }

    /**
     * Obtiene un producto concreto por su identificador.
     */
    suspend fun obtenerProducto(id: Long): ProductoDTO {
        return api.obtenerProducto(id)
    }

    /**
     * Crea un nuevo producto y devuelve el recurso creado devuelto por el servidor.
     */
    suspend fun crearProducto(producto: ProductoDTO): ProductoDTO {
        val response: Response<ProductoDTO> = api.crearProducto(producto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta sin cuerpo al crear producto")
        } else {
            throw Exception("Error al crear producto: código ${'$'}{response.code()}")
        }
    }

    /**
     * Actualiza un producto existente.
     * Devuelve el producto actualizado que devuelve el backend.
     */
    suspend fun actualizarProducto(id: Long, producto: ProductoDTO): ProductoDTO {
        val response: Response<ProductoDTO> = api.actualizarProducto(id, producto)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta sin cuerpo al actualizar producto")
        } else {
            throw Exception("Error al actualizar producto: código ${'$'}{response.code()}")
        }
    }

    /**
     * Elimina un producto por su identificador.
     */
    suspend fun eliminarProducto(id: Long) {
        val response: Response<Unit> = api.eliminarProducto(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar producto: código ${'$'}{response.code()}")
        }
    }
}