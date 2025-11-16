package com.example.levelup_gamerapp.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LevelUpApi {

    @POST("api/pedidos")
    suspend fun crearPedido(
        @Body request: CrearPedidoRequest
    ): Response<PedidoResponseDTO>   // o Response<Void> si no quieres procesar respuesta
}
