package com.example.levelup_gamerapp.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow   // 👈 IMPORTANTE

@Dao
interface ProductosDao {

    // Lista “normal” (suspend)
    @Query("SELECT * FROM productos")
    suspend fun obtenerTodos(): List<ProductosEntity>

    // 🔹 NUEVO: flujo reactivo para observar cambios en la tabla
    @Query("SELECT * FROM productos")
    fun observarTodos(): Flow<List<ProductosEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductosEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductosEntity)

    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()

    // Obtener producto por ID
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerProductoPorId(id: Int): ProductosEntity?

    // Buscar producto por nombre (para la compra)
    @Query("SELECT * FROM productos WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerProductoPorNombre(nombre: String): ProductosEntity?

    // Actualizar producto (para descontar stock)
    @Update
    suspend fun actualizarProducto(producto: ProductosEntity)
}
