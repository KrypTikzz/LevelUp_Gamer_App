package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.ProductoDTO
import retrofit2.Response

/**
 * repositorio remoto para gestionar productos a través del backend.
 * utiliza la api general (levelUpApi) y envía el jwt automáticamente
 * mediante el interceptor configurado en apiclient.
 */
open class RemoteProductosRepository {

    // api general del backend (productos, categorías, usuarios, etc.)
    private val api = ApiClient.levelUpApi

    /**
     * obtiene el listado completo de productos desde el servidor.
     */
    open suspend fun obtenerProductos(): List<ProductoDTO> {
        return api.obtenerProductos()
    }

    /**
     * obtiene un producto concreto por su identificador.
     */
    open suspend fun obtenerProducto(id: Long): ProductoDTO {
        return api.obtenerProducto(id)
    }

    /**
     * crea un nuevo producto y devuelve el recurso creado por el backend.
     */
    open suspend fun crearProducto(producto: ProductoDTO): ProductoDTO {
        val response: Response<ProductoDTO> = api.crearProducto(producto)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("respuesta sin cuerpo al crear producto")
        } else {
            throw Exception("error al crear producto: código ${response.code()}")
        }
    }

    /**
     * actualiza un producto existente.
     */
    open suspend fun actualizarProducto(id: Long, producto: ProductoDTO): ProductoDTO {
        val response: Response<ProductoDTO> = api.actualizarProducto(id, producto)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("respuesta sin cuerpo al actualizar producto")
        } else {
            throw Exception("error al actualizar producto: código ${response.code()}")
        }
    }

    /**
     * elimina un producto por su identificador.
     */
    open suspend fun eliminarProducto(id: Long) {
        val response: Response<Unit> = api.eliminarProducto(id)
        if (!response.isSuccessful) {
            throw Exception("error al eliminar producto: código ${response.code()}")
        }
    }
}
