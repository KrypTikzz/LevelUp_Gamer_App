package com.example.levelup_gamerapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un producto almacenado en la base de datos local.  Además del
 * identificador interno [id] que genera Room automáticamente, se añade el
 * campo [remoteId] para guardar el identificador real del producto en el
 * backend. Cuando se envían pedidos al servidor este campo se utiliza para
 * asociar cada ítem con el producto correcto.
 */
@Entity(tableName = "productos")
data class ProductosEntity(
    /** Identificador autogenerado por Room. */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    /**
     * Identificador del producto en el backend. Puede ser nulo si el
     * producto todavía no existe en el servidor.
     */
    val remoteId: Long? = null,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val cantidadDisponible: Int
)
