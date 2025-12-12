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
import com.example.levelup_gamerapp.core.UserSession // Importamos UserSession
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.CarritoEntity
import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CrearPedidoRequest
import com.example.levelup_gamerapp.remote.ItemPedidoRequest
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra los ítems del carrito y permite confirmar la compra.
 * Se eliminan las validaciones de stock locales para evitar fallos cuando el
 * catálogo local no coincide con el del backend. Tras confirmar la compra se
 * ya no actualiza la base local ni valida stock: la gestión del inventario
 * se delega completamente al backend. Tras registrar la compra se vacía
 * el carrito local.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito() {
    val context = androidx.compose.ui.platform.LocalContext.current

    // DAOs y repositorio del carrito
    val db = AppDatabase.obtenerBaseDatos(context)
    val carritoDao = db.carritoDao()
    val repo = CarritoRepository(carritoDao)
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(repo))

    // NOTA: Ya no necesitamos SesionViewModel aquí porque leeremos directo de UserSession
    // para asegurar que tenemos el ID más reciente tras el Login/Registro.

    val carrito by carritoVM.carrito.collectAsState(initial = emptyList())

    // Agrupar por idProducto para sumar cantidades
    val carritoAgrupado = remember(carrito) {
        carrito
            .groupBy { it.idProducto }
            .map { (_, items) ->
                val primero = items.first()
                val cantidadTotal = items.sumOf { it.cantidad }
                primero.copy(cantidad = cantidadTotal)
            }
    }

    // Total que se mostrará
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

                                    // CAMBIO PRINCIPAL: Usamos UserSession directamente
                                    // Esto garantiza que si acabas de loguearte, el ID esté disponible.
                                    val usuarioId = UserSession.idUsuario

                                    if (usuarioId == null) {
                                        snackbarHostState.showSnackbar("Debes iniciar sesión para comprar")
                                        return@launch
                                    }

                                    // Construir DTO para el backend
                                    val itemsDto = carritoAgrupado.map { item ->
                                        ItemPedidoRequest(
                                            productoId = item.idProducto,
                                            cantidad = item.cantidad
                                        )
                                    }

                                    val request = CrearPedidoRequest(
                                        usuarioId = usuarioId,
                                        items = itemsDto
                                    )

                                    try {
                                        // Llamada a la API usando 'api' (que ya tiene el interceptor configurado)
                                        val response = ApiClient.levelUpApi.crearPedido(request) //

                                        if (response.isSuccessful) {
                                            carritoVM.vaciarCarrito()
                                            snackbarHostState.showSnackbar("Compra registrada en el servidor ✅")
                                        } else {
                                            snackbarHostState.showSnackbar(
                                                "Error al registrar compra (código ${response.code()})"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
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
 * Elemento individual dentro del carrito. Muestra la imagen, nombre, cantidad y
 * precio del producto, así como un botón para eliminarlo del carrito.
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