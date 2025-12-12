package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import kotlinx.coroutines.launch

@Composable
fun PantallaEditarProducto(navController: NavController, id: Long) {
    val context = LocalContext.current
    val repo = remember { RemoteProductosRepository() }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    var loading by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf<String?>(null) }

    var productoActual by remember { mutableStateOf<ProductoDTO?>(null) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Al cargar el producto inicial, actualiza los estados de los campos
    LaunchedEffect(productoActual) {
        productoActual?.let { prod ->
            nombre = prod.nombreProducto
            descripcion = prod.descripcionProducto
            precio = prod.precioProducto.toString()
            imagenUrl = prod.imagenUrl
            categoria = prod.categoriaProducto
            stock = prod.cantidadDisponible.toString()
        }
    }

    LaunchedEffect(id) {
        if (id > 0) {
            try {
                productoActual = repo.obtenerProducto(id)
            } catch (e: Exception) {
                errorCarga = e.message ?: "Error desconocido"
            } finally {
                loading = false
            }
        } else {
            // Creación de producto nuevo (no se carga nada desde el backend)
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (id > 0) "Editar producto" else "Crear producto",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> {
                Text("Cargando producto…")
            }
            errorCarga != null -> {
                Text("Error al cargar: $errorCarga")
            }
            else -> {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imagenUrl,
                    onValueChange = { imagenUrl = it },
                    label = { Text("URL de imagen") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val precioDouble = precio.toDoubleOrNull()
                        val stockInt = stock.toIntOrNull()

                        if (nombre.isBlank() || descripcion.isBlank() ||
                            precioDouble == null || stockInt == null
                        ) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Completa los campos correctamente")
                            }
                            return@Button
                        }

                        val nuevoProducto = ProductoDTO(
                            id = productoActual?.id,
                            nombreProducto = nombre.trim(),
                            descripcionProducto = descripcion.trim(),
                            precioProducto = precioDouble,
                            imagenUrl = imagenUrl.ifBlank { "https://via.placeholder.com/300" },
                            cantidadDisponible = stockInt,
                            categoriaId = productoActual?.categoriaId ?: 0L,
                            categoriaProducto = categoria.ifBlank { productoActual?.categoriaProducto ?: "General" }
                        )

                        scope.launch {
                            try {
                                if (id > 0) {
                                    val prodId = productoActual?.id ?: id
                                    repo.actualizarProducto(prodId, nuevoProducto)
                                    snackbarHostState.showSnackbar("Producto actualizado correctamente")
                                } else {
                                    repo.crearProducto(nuevoProducto)
                                    snackbarHostState.showSnackbar("Producto creado correctamente")
                                }
                                navController.popBackStack()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error al guardar: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (id > 0) "Guardar cambios" else "Crear")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}
