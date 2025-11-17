package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.NoticiasApiClient
import com.example.levelup_gamerapp.remote.NoticiaMmoBombDTO

class NoticiasRepository(
    private val apiClient: NoticiasApiClient = NoticiasApiClient
) {
    suspend fun obtenerNoticias(): List<NoticiaMmoBombDTO> {
        return apiClient.api.getLatestNews()
    }
}
