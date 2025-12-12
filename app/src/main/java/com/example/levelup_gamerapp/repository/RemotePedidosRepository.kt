package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.PedidoResponseDTO

open class RemotePedidosRepository {

    private val api = ApiClient.levelUpApi

    open suspend fun listarPorUsuario(usuarioId: Long): List<PedidoResponseDTO> {
        return api.listarPedidosPorUsuario(usuarioId)
    }

    open suspend fun obtenerPorId(id: Long): PedidoResponseDTO {
        return api.obtenerPedido(id)
    }

    open suspend fun listarTodos(): List<PedidoResponseDTO> {
        return api.listarPedidos()
    }
}
