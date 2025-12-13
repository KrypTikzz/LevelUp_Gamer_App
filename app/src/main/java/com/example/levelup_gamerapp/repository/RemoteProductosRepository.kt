package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.utils.ErrorUtils
import retrofit2.HttpException
import retrofit2.Response

/**
 * repositorio remoto para gestionar productos a través del backend.
 * utiliza la api general (levelUpApi) y envía el jwt automáticamente
 * mediante el interceptor configurado en apiclient.
 */
open class RemoteProductosRepository {

    private val api = ApiClient.levelUpApi

    /**
     * obtiene el listado completo de productos desde el servidor.
     */
    open suspend fun obtenerProductos(): List<ProductoDTO> {
        return try {
            api.obtenerProductos()
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Productos"))
        }
    }

    /**
     * obtiene un producto concreto por su identificador.
     */
    open suspend fun obtenerProducto(id: Long): ProductoDTO {
        return try {
            api.obtenerProducto(id)
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Producto"))
        }
    }

    /**
     * crea un nuevo producto y devuelve el recurso creado por el backend.
     */
    open suspend fun crearProducto(producto: ProductoDTO): ProductoDTO {
        val response: Response<ProductoDTO> = api.crearProducto(producto)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta inválida del servidor (producto sin cuerpo).")
        } else {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Producto"))
        }
    }

    /**
     * actualiza un producto existente.
     */
    open suspend fun actualizarProducto(id: Long, producto: ProductoDTO): ProductoDTO {
        val response: Response<ProductoDTO> = api.actualizarProducto(id, producto)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Respuesta inválida del servidor (producto sin cuerpo).")
        } else {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Producto"))
        }
    }

    /**
     * elimina un producto por su identificador.
     */
    open suspend fun eliminarProducto(id: Long) {
        val response: Response<Unit> = api.eliminarProducto(id)
        if (!response.isSuccessful) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(response.code(), "Producto"))
        }
    }
}
