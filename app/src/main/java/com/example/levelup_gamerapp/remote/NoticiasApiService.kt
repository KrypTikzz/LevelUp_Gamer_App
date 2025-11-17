package com.example.levelup_gamerapp.remote

import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// DTO que mapea solo los campos que nos interesan de la API de MMOBomb.
// La API devuelve más campos, pero los demás se ignoran sin problema.
data class NoticiaMmoBombDTO(
    val id: Int,
    val title: String,
    val short_description: String,
    val thumbnail: String,
    val article_url: String
)

interface NoticiasApiService {
    @GET("api1/latestnews")
    suspend fun getLatestNews(): List<NoticiaMmoBombDTO>
}

object NoticiasApiClient {
    private const val BASE_URL = "https://www.mmobomb.com/"

    val api: NoticiasApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoticiasApiService::class.java)
    }
}
