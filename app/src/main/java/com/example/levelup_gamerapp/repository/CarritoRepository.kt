package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.local.CarritoDao
import com.example.levelup_gamerapp.local.CarritoEntity
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val dao: CarritoDao) {

    val carrito: Flow<List<CarritoEntity>> = dao.obtenerCarrito()

    suspend fun agregar(item: CarritoEntity) {
        dao.agregarAlCarrito(item)
    }

    suspend fun eliminar(item: CarritoEntity) {
        dao.eliminarDelCarrito(item)
    }

    suspend fun vaciar() {
        dao.vaciarCarrito()
    }
}

