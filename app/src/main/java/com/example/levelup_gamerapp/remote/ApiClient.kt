package com.example.levelup_gamerapp.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Configura el cliente de Retrofit para acceder al backend de LevelUp Gamer. La
 * URL base apunta a `10.0.2.2:8080`, que es la forma de acceder al host
 * local cuando la app se ejecuta en un emulador Android. Para dispositivos
 * físicos o servidores remotos se debe ajustar esta URL según corresponda.
 */
object ApiClient {
    // Dirección base del backend. Cambia según la configuración de tu servidor.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    /**
     * Instancia perezosa de la interfaz de la API. Se crea una sola vez y se
     * reutiliza para todas las llamadas.
     */
    val api: LevelUpApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LevelUpApi::class.java)
    }
}