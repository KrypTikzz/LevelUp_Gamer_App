package com.example.levelup_gamerapp.remote

import com.example.levelup_gamerapp.core.AuthInterceptor
import com.example.levelup_gamerapp.remote.api.AuthApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * configura el cliente de retrofit para acceder al backend de levelup gamer.
 * incorpora un interceptor para enviar el token jwt automáticamente
 * en cada request autenticada.
 */
object ApiClient {

    // url base del backend
    private const val BASE_URL = "http://10.0.2.2:8080/"

    /**
     * cliente http con interceptor jwt
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    /**
     * instancia única de retrofit
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * api de autenticación (login / registro)
     */
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    /**
     * api general de la aplicación (productos, categorías, etc.)
     */
    val levelUpApi: LevelUpApi by lazy {
        retrofit.create(LevelUpApi::class.java)
    }
}
