package com.example.levelup_gamerapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un ítem del carrito.  A diferencia de la versión
 * original, se incluye el campo [idProducto] que almacena el identificador
 * real del producto.  Esto permite asociar cada ítem con el producto
 * correspondiente en la base de datos remota y enviar correctamente la
 * compra al backend.
 */
@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** Identificador del producto en la tabla de productos. */
    val idProducto: Long,

    /** Nombre del producto. */
    val nombreProducto: String,

    /** Precio unitario del producto. */
    val precio: Double,

    /** Cantidad de unidades añadidas al carrito. */
    val cantidad: Int,

    /** URL de la imagen del producto. */
    val imagenUrl: String
)

