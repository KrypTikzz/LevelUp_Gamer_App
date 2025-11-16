package com.example.levelup_gamerapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreProducto: String,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String
)

