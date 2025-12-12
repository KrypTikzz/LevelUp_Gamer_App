package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.dto.LoginRequestDTO

class LoginRepository {

    suspend fun login(correo: String, contrasena: String) =
        ApiClient.authApi.login(
            LoginRequestDTO(
                correo = correo,
                contrasena = contrasena
            )
        )
}
