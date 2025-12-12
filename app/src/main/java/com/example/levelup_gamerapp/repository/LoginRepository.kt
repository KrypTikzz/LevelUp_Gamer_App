package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.core.UserSession
import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.dto.AuthResponseDTO
import com.example.levelup_gamerapp.remote.dto.LoginRequestDTO

class LoginRepository {

    suspend fun login(correo: String, contrasena: String): AuthResponseDTO {
        // 1. Llamamos al endpoint de Login
        val response = ApiClient.authApi.login(
            LoginRequestDTO(
                correo = correo,
                contrasena = contrasena
            )
        )

        // 2. ¡IMPORTANTE! Guardamos TODOS los datos en la sesión
        UserSession.token = response.token
        UserSession.idUsuario = response.idUsuario
        UserSession.nombreCompleto = response.nombreCompleto
        UserSession.correo = response.correo
        UserSession.rol = response.rol

        return response
    }
}