package com.example.levelup_gamerapp.remote

/**
 * Representación de un producto tal como lo expone el backend. Los nombres de
 * las propiedades siguen la convención del DTO de Spring para facilitar el
 * mapeo con Retrofit y Gson. Cuando se consume este DTO desde la app se
 * utilizan estos mismos nombres.
 */
data class ProductoDTO(
    val id: Long? = null,
    /** Nombre del producto en el backend. */
    val nombreProducto: String,
    /** Descripción detallada del producto. */
    val descripcionProducto: String,
    /** Precio de venta del producto. */
    val precioProducto: Double,
    /** URL de la imagen del producto. */
    val imagenUrl: String,
    /** Cantidad disponible en stock del producto. */
    val cantidadDisponible: Int,
    /** Identificador de la categoría a la que pertenece el producto. */
    val categoriaId: Long,
    /** Nombre de la categoría del producto. */
    val categoriaProducto: String
)