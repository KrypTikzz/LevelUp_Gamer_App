package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.local.ProductosDao
import com.example.levelup_gamerapp.local.ProductosEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que encapsula el acceso a los productos almacenados en la base de
 * datos local. Todas las operaciones de lectura y escritura se delegan al DAO.
 *
 * Se añade un métodoO para obtener un producto por su identificador remoto,
 * permitiendo que la app utilice el ID proveniente del backend para localizar
 * productos cuando se realizan pedidos.
 */
class ProductosRepository(private val dao: ProductosDao) {

    /** Devuelve la lista completa de productos. */
    suspend fun obtenerProductos(): List<ProductosEntity> {
        return dao.obtenerTodos()
    }

    /** Devuelve un flujo reactivo de la lista de productos. */
    fun observarProductos(): Flow<List<ProductosEntity>> = dao.observarTodos()

    /** Inserta o actualiza un producto. */
    suspend fun insertarProducto(producto: ProductosEntity) {
        dao.insertarProducto(producto)
    }

    /** Elimina un producto específico. */
    suspend fun eliminarProducto(producto: ProductosEntity) {
        dao.eliminarProducto(producto)
    }

    /** Elimina todos los productos. */
    suspend fun eliminarTodos() {
        dao.eliminarTodos()
    }

    /** Obtiene un producto por su identificador local. */
    suspend fun obtenerProductoPorId(id: Int): ProductosEntity? {
        return dao.obtenerProductoPorId(id)
    }

    /** Obtiene un producto por su identificador remoto. */
    suspend fun obtenerProductoPorRemoteId(remoteId: Long): ProductosEntity? {
        return dao.obtenerProductoPorRemoteId(remoteId)
    }
}