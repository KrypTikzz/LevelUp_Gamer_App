package com.example.levelup_gamerapp.remote

data class ItemPedidoRequest(
    val productoId: Long,
    val cantidad: Int
)

data class CrearPedidoRequest(
    val usuarioId: Long,
    val items: List<ItemPedidoRequest>
)

// Opcional: si quieres leer la respuesta del backend
data class DetallePedidoDTO(
    val productoId: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

data class PedidoResponseDTO(
    val id: Long,
    val usuarioId: Long,
    val correoUsuario: String,
    val fechaCreacion: String,
    val total: Double,
    val detalles: List<DetallePedidoDTO>
)
