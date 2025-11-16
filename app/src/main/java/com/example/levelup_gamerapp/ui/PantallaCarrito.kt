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
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import kotlinx.coroutines.launch   // 🔹 NUEVO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito() {
    val context = androidx.compose.ui.platform.LocalContext.current

    // DAOs y repositorio del carrito
    val db = AppDatabase.obtenerBaseDatos(context)
    val carritoDao = db.carritoDao()
    val repo = CarritoRepository(carritoDao)
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(repo))

    // 🔹 DAO de productos para poder descontar stock
    val productosDao = db.productosDao()

    val carrito by carritoVM.carrito.collectAsState(initial = emptyList())

    // Agrupar productos por nombre, precio e imagen para combinar cantidades
    val carritoAgrupado = remember(carrito) {
        carrito.groupBy { Triple(it.nombreProducto, it.precio, it.imagenUrl) }
            .map { (clave, items) ->
                val (nombre, precio, imagenUrl) = clave
                val cantidadTotal = items.sumOf { it.cantidad }
                items.first().copy(cantidad = cantidadTotal)
            }
    }

    val total = carrito.sumOf { it.precio * it.cantidad }

    // 🔹 Para lanzar corrutinas desde la UI
    val scope = rememberCoroutineScope()

    // 🔹 Para mostrar mensajes
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
            // 🔹 Host personalizado para que el snackbar sea oscuro con texto legible
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
                                // 🔹 Lógica de compra con validación de stock
                                scope.launch {
                                    if (carritoAgrupado.isEmpty()) {
                                        snackbarHostState.showSnackbar("No hay productos en el carrito")
                                        return@launch
                                    }

                                    // 1) Verificar stock
                                    var errorMensaje: String? = null
                                    for (item in carritoAgrupado) {
                                        val producto =
                                            productosDao.obtenerProductoPorNombre(item.nombreProducto)
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

                                    // 2) Descontar stock
                                    for (item in carritoAgrupado) {
                                        val producto =
                                            productosDao.obtenerProductoPorNombre(item.nombreProducto)
                                        if (producto != null) {
                                            val actualizado = producto.copy(
                                                cantidadDisponible = producto.cantidadDisponible - item.cantidad
                                            )
                                            productosDao.actualizarProducto(actualizado)
                                        }
                                    }

                                    // 3) Vaciar carrito
                                    carritoVM.vaciarCarrito()

                                    // 4) Avisar al usuario
                                    snackbarHostState.showSnackbar("Compra realizada con éxito ✅")
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
