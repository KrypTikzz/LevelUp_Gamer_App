package com.example.levelup_gamerapp.remote.dto

data class RegisterRequestDTO(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val fechaNacimiento: String, // Formato YYYY-MM-DD
    val telefono: String = "",
    val region: String = "",
    val comuna: String = ""
)