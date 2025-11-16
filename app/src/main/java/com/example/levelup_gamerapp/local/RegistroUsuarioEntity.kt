package com.example.levelup_gamerapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levelup_database")
data class RegistroUsuarioEntity(
       @PrimaryKey(autoGenerate = true)
       val id: Int = 0,
       val nombre: String,
       val apellido: String,
       val correo: String,
       val contrasena: String,
       val edad: Int,
       val descuentoAplicado: Int = 0,
       val fotoPerfil: ByteArray? = null
)