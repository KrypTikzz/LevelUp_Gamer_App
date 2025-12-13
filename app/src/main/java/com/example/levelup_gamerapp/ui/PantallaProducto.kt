package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory

/**
 * Pantalla de detalle de un producto.
 * Ahora carga 1 producto por ID desde el backend: GET /api/productos/{id}
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProducto(
    id: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Carrito local (Room)
    val db = remember { AppDatabase.getDatabase(context) }
    val carritoRepo = remember { CarritoRepository(db.carritoDao()) }
    val carritoViewModel: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(carritoRepo))

    // Repositorio remoto de productos
    val productosRepo = remember { RemoteProductosRepository() }

    var producto by remember { mutableStateOf<ProductoDTO?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Cargar producto por ID (no cargar toda la lista)
    LaunchedEffect(id) {
        cargando = true
        errorMsg = null
        producto = null
        try {
            producto = productosRepo.obtenerProducto(id)
        } catch (e: Exception) {
            errorMsg = e.localizedMessage ?: "Error al cargar producto"
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de producto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->

        when {
            cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF39FF14))
                }
            }

            errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMsg ?: "Error desconocido",
                        color = Color.White
                    )
                }
            }

            producto == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Producto no encontrado", color = Color.White)
                }
            }

            else -> {
                val p = producto!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    // Imagen
                    Image(
                        painter = rememberAsyncImagePainter(p.imagenUrl),
                        contentDescription = p.nombreProducto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = p.nombreProducto,
                        color = Color(0xFF39FF14),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$${p.precioProducto}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = p.descripcionProducto,
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Stock disponible: ${p.cantidadDisponible}",
                        color = Color(0xFF1E90FF),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            carritoViewModel.agregarProductoAlCarrito(
                                idProducto = p.id ?: id,
                                nombre = p.nombreProducto,
                                precio = p.precioProducto,
                                imagenUrl = p.imagenUrl
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = p.cantidadDisponible > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF39FF14),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(if (p.cantidadDisponible > 0) "Agregar al carrito" else "Sin stock")
                    }
                }
            }
        }
    }
}
