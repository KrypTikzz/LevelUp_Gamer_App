package com.example.levelup_gamerapp.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito")
    fun obtenerCarrito(): Flow<List<CarritoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarAlCarrito(item: CarritoEntity)

    @Delete
    suspend fun eliminarDelCarrito(item: CarritoEntity)

    @Query("DELETE FROM carrito")
    suspend fun vaciarCarrito()
}

