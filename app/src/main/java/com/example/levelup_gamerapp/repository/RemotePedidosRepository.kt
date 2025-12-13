package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.PedidoResponseDTO
import com.example.levelup_gamerapp.utils.ErrorUtils
import retrofit2.HttpException

open class RemotePedidosRepository {

    private val api = ApiClient.levelUpApi

    open suspend fun listarPorUsuario(usuarioId: Long): List<PedidoResponseDTO> {
        return try {
            api.listarPedidosPorUsuario(usuarioId)
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Pedidos"))
        }
    }

    open suspend fun obtenerPorId(id: Long): PedidoResponseDTO {
        return try {
            api.obtenerPedido(id)
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Pedido"))
        }
    }

    open suspend fun listarTodos(): List<PedidoResponseDTO> {
        return try {
            api.listarPedidos()
        } catch (e: HttpException) {
            throw Exception(ErrorUtils.traducirCodigoHTTP(e.code(), "Pedidos"))
        }
    }
}
