package com.example.levelup_gamerapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductosEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val cantidadDisponible: Int // 🆕 nuevo campo
)

