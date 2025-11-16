package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.CarritoEntity
import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CrearPedidoRequest
import com.example.levelup_gamerapp.remote.ItemPedidoRequest
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito() {
    val context = androidx.compose.ui.platform.LocalContext.current

    // DAOs y repositorio del carrito
    val db = AppDatabase.obtenerBaseDatos(context)
    val carritoDao = db.carritoDao()
    val repo = CarritoRepository(carritoDao)
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(repo))

    // DAO de productos para poder validar y descontar stock local
    val productosDao = db.productosDao()

    val carrito by carritoVM.carrito.collectAsState(initial = emptyList())

    // Agrupar por idProducto (no por nombre/precio/imagen)
    val carritoAgrupado = remember(carrito) {
        carrito
            .groupBy { it.idProducto } // clave: id real del producto
            .map { (_, items) ->
                val primero = items.first()
                val cantidadTotal = items.sumOf { it.cantidad }
                primero.copy(cantidad = cantidadTotal)
            }
    }

    // Total coherente con lo que se muestra
    val total = carritoAgrupado.sumOf { it.precio * it.cantidad }

    // Corrutinas y snackbars
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🛒 Carrito de Compras",
                        color = Color(0xFF39FF14),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF111111),
                    contentColor = Color.White,
                    actionColor = Color(0xFF39FF14)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            if (carrito.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío 🛍️", color = Color.White, fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    items(carritoAgrupado) { item ->
                        CarritoItem(
                            item = item,
                            onEliminar = { carritoVM.eliminarProducto(item) }
                        )
                    }
                }

                Divider(color = Color(0xFF39FF14), thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total: $${"%.2f".format(total)}",
                        color = Color(0xFF1E90FF),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { carritoVM.vaciarCarrito() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B3B))
                        ) {
                            Text("Vaciar", color = Color.White)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    if (carritoAgrupado.isEmpty()) {
                                        snackbarHostState.showSnackbar("No hay productos en el carrito")
                                        return@launch
                                    }

                                    // 1) Verificar stock local usando idProducto
                                    var errorMensaje: String? = null
                                    for (item in carritoAgrupado) {
                                        val producto =
                                            productosDao.obtenerProductoPorId(item.idProducto.toInt())
                                        if (producto == null) {
                                            errorMensaje =
                                                "Producto '${item.nombreProducto}' no encontrado en catálogo."
                                            break
                                        }
                                        if (producto.cantidadDisponible < item.cantidad) {
                                            errorMensaje =
                                                "No hay stock suficiente de '${item.nombreProducto}'. Disponible: ${producto.cantidadDisponible}, solicitado: ${item.cantidad}"
                                            break
                                        }
                                    }

                                    if (errorMensaje != null) {
                                        snackbarHostState.showSnackbar(errorMensaje!!)
                                        return@launch
                                    }

                                    // 2) Armar DTO para el backend (items del pedido)
                                    val itemsDto = carritoAgrupado.map { item ->
                                        ItemPedidoRequest(
                                            productoId = item.idProducto,
                                            cantidad = item.cantidad
                                        )
                                    }

                                    // 👇 Por ahora, usuario fijo: usa el id que viste en pgAdmin
                                    val usuarioId = 1L // <-- CAMBIA este 1L por el id real de tu usuario

                                    val request = CrearPedidoRequest(
                                        usuarioId = usuarioId,
                                        items = itemsDto
                                    )

                                    try {
                                        // 3) Llamar al backend (POST /api/pedidos)
                                        val response = ApiClient.api.crearPedido(request)

                                        if (response.isSuccessful) {
                                            // 4) Descontar stock local para reflejar el cambio
                                            for (item in carritoAgrupado) {
                                                val producto =
                                                    productosDao.obtenerProductoPorId(item.idProducto.toInt())
                                                if (producto != null) {
                                                    val actualizado = producto.copy(
                                                        cantidadDisponible = producto.cantidadDisponible - item.cantidad
                                                    )
                                                    productosDao.actualizarProducto(actualizado)
                                                }
                                            }

                                            // 5) Vaciar carrito local
                                            carritoVM.vaciarCarrito()

                                            // 6) Avisar al usuario
                                            snackbarHostState.showSnackbar("Compra registrada en el servidor ✅")
                                        } else {
                                            snackbarHostState.showSnackbar(
                                                "Error al registrar compra (código ${response.code()})"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("No se pudo contactar al servidor 😕")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14))
                        ) {
                            Text("Comprar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 🔹 Elemento individual dentro del carrito
 */
@Composable
fun CarritoItem(item: CarritoEntity, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.imagenUrl),
                contentDescription = item.nombreProducto,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 10.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(item.nombreProducto, color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
                Text("Cantidad: ${item.cantidad}", color = Color.White)
                Text("Precio: $${item.precio}", color = Color(0xFF1E90FF))
            }

            Button(
                onClick = onEliminar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B3B))
            ) {
                Text("🗑️", color = Color.White)
            }
        }
    }
}
