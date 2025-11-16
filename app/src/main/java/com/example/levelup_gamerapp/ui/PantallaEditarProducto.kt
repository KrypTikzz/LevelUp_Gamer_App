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

/**
 * Pantalla para editar o crear un producto utilizando el backend como fuente de datos.
 * En lugar de apoyarse en Room y un ViewModel local, esta versión consulta y
 * actualiza directamente a través de [RemoteProductosRepository].
 *
 * Si el `id` que se pasa es mayor que cero se considerará que se está editando un
 * producto existente, el cual se cargará desde el servidor. En caso contrario,
 * se tomará como creación de un nuevo producto.
 *
 * @param navController controlador de navegación para volver a la lista de productos.
 * @param id identificador del producto a editar (usar un valor negativo para crear).
 */
@Composable
fun PantallaEditarProducto(navController: NavController, id: Int) {
    val repo = remember { RemoteProductosRepository() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados para la consulta del producto existente
    var loading by remember { mutableStateOf(id > 0) }
    var productoActual by remember { mutableStateOf<ProductoDTO?>(null) }
    var errorCarga by remember { mutableStateOf<String?>(null) }

    // Estados para los campos de texto
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Al cargar el producto inicial, actualiza los estados de los campos
    LaunchedEffect(productoActual) {
        productoActual?.let { prod ->
            // Asignamos los valores provenientes del DTO remoto
            nombre = prod.nombreProducto
            descripcion = prod.descripcionProducto
            precio = prod.precioProducto.toString()
            imagenUrl = prod.imagenUrl
            categoria = prod.categoriaProducto
            stock = prod.cantidadDisponible.toString()
        }
    }

    // Recuperar producto existente cuando el id es válido (> 0)
    LaunchedEffect(id) {
        if (id > 0) {
            try {
                productoActual = repo.obtenerProducto(id.toLong())
            } catch (e: Exception) {
                errorCarga = e.message ?: "Error desconocido"
            } finally {
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = if (id > 0) "Editar Producto" else "Nuevo Producto",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // Mostrar estados de carga o error
        when {
            loading -> {
                Text("Cargando producto…")
            }
            errorCarga != null -> {
                Text("Error al cargar: ${'$'}errorCarga")
            }
            else -> {
                // Campos editables
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
                    label = { Text("Stock disponible") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                // Botón para guardar cambios o crear
                Button(
                    onClick = {
                        // Validaciones mínimas
                        val precioDouble = precio.toDoubleOrNull()
                        val stockInt = stock.toIntOrNull()
                        if (precioDouble != null && stockInt != null) {
                            // Construir el DTO actualizado o nuevo
                            // Construir el DTO actualizado o nuevo.
                            // Utilizamos las mismas propiedades que el DTO remoto expone.
                            val nuevoProducto = ProductoDTO(
                                id = productoActual?.id,
                                nombreProducto = nombre.trim(),
                                descripcionProducto = descripcion.trim(),
                                precioProducto = precioDouble,
                                imagenUrl = imagenUrl.ifBlank { "https://via.placeholder.com/300" },
                                cantidadDisponible = stockInt,
                                // Si estamos editando reutilizamos el categoriaId existente, de lo contrario dejamos 0L.
                                categoriaId = productoActual?.categoriaId ?: 0L,
                                categoriaProducto = categoria.ifBlank { productoActual?.categoriaProducto ?: "General" }
                            )
                            scope.launch {
                                try {
                                    if (id > 0) {
                                        // Al editar, actualizamos usando el id original del producto
                                        val prodId = productoActual?.id ?: 0L
                                        repo.actualizarProducto(prodId, nuevoProducto)
                                        snackbarHostState.showSnackbar("Producto actualizado correctamente")
                                    } else {
                                        repo.crearProducto(nuevoProducto)
                                        snackbarHostState.showSnackbar("Producto creado correctamente")
                                    }
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error al guardar: ${'$'}{e.message}")
                                }
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Precio o stock no válido")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (id > 0) "Guardar cambios" else "Crear producto")
                }

                Spacer(modifier = Modifier.padding(4.dp))

                // Botón cancelar
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }

        // Snackbar host para mensajes
        SnackbarHost(hostState = snackbarHostState)
    }
}