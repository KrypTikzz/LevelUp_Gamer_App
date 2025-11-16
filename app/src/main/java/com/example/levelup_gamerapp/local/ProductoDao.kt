package com.example.levelup_gamerapp.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [ProductosEntity].
 *
 * Proporciona métodos para obtener, insertar, actualizar y eliminar productos
 * en la base de datos local. Además añade la capacidad de buscar por el
 * identificador remoto ([ProductosEntity.remoteId]) para sincronizar con el
 * backend. De este modo, cuando el usuario realiza una compra la app puede
 * localizar el producto correcto y descontar el stock aunque el ID local
 * difiera del ID remoto.
 */
@Dao
interface ProductosDao {
    /**
     * Devuelve la lista completa de productos almacenados. Operación de
     * suspensión (bloqueante).
     */
    @Query("SELECT * FROM productos")
    suspend fun obtenerTodos(): List<ProductosEntity>

    /**
     * Observa la tabla de productos y emite una nueva lista cada vez que se
     * produce un cambio. Ideal para usar con Compose.
     */
    @Query("SELECT * FROM productos")
    fun observarTodos(): Flow<List<ProductosEntity>>

    /** Inserta o reemplaza un producto en la base de datos. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductosEntity)

    /** Elimina un producto específico. */
    @Delete
    suspend fun eliminarProducto(producto: ProductosEntity)

    /** Elimina todos los productos almacenados. */
    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()

    /** Obtiene un producto por su identificador local (clave primaria de Room). */
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerProductoPorId(id: Int): ProductosEntity?

    /**
     * Busca un producto por su nombre. Utilizado principalmente en la
     * pantalla de compra para verificar la disponibilidad local.
     */
    @Query("SELECT * FROM productos WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerProductoPorNombre(nombre: String): ProductosEntity?

    /** Actualiza un producto existente. */
    @Update
    suspend fun actualizarProducto(producto: ProductosEntity)

    /**
     * Obtiene un producto por su identificador remoto. Devuelve null si no
     * existe ningún producto con ese remoteId. Este método es útil cuando
     * queremos reflejar en la base local el stock de un producto que se
     * identifica mediante un ID remoto proveniente del backend.
     */
    @Query("SELECT * FROM productos WHERE remoteId = :remoteId LIMIT 1")
    suspend fun obtenerProductoPorRemoteId(remoteId: Long): ProductosEntity?
}