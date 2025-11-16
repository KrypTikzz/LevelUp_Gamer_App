package com.example.levelup_gamerapp.remote

/**
 * Representación de una categoría tal como la expone el backend. Incluye
 * identificador, nombre y descripción. Se utiliza en las llamadas a la API
 * para listar o crear categorías.
 */
data class CategoriaDTO(
    val id: Long? = null,
    /** Nombre de la categoría. */
    val nombreCategoria: String,
    /** Descripción de la categoría. */
    val descripcionCategoria: String
)