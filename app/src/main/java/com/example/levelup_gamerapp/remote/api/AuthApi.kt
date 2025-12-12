package com.example.levelup_gamerapp.remote.api

import com.example.levelup_gamerapp.remote.dto.AuthResponseDTO
import com.example.levelup_gamerapp.remote.dto.LoginRequestDTO
import com.example.levelup_gamerapp.remote.dto.RegisterRequestDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDTO
    ): AuthResponseDTO

    // Agregamos el endpoint de registro público
    @POST("api/auth/registro")
    suspend fun registrar(
        @Body request: RegisterRequestDTO
    ): AuthResponseDTO
}
