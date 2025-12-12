package com.example.levelup_gamerapp.remote.dto

data class AuthResponseDTO(
    val token: String,
    val idUsuario: Long,
    val nombreCompleto: String,
    val correo: String,
    val rol: String
)
