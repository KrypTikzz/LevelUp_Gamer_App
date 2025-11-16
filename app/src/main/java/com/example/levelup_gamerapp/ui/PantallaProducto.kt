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
import kotlinx.coroutines.launch

/**
 * Pantalla de detalle de un producto.
 * Esta versión obtiene los datos del producto desde el backend mediante
 * [RemoteProductosRepository] y mantiene el carrito de forma local.
 *
 * @param id Identificador del producto que se quiere mostrar.
 * @param onNavigateBack Acción a ejecutar al volver atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProducto(
    id: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    // Repositorio y ViewModel del carrito (local)
    val db = AppDatabase.obtenerBaseDatos(context)
    val cRepo = remember { CarritoRepository(db.carritoDao()) }
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(cRepo))

    // Repositorio remoto para productos
    val productosRepo = remember { RemoteProductosRepository() }
    var listaProductos by remember { mutableStateOf<List<ProductoDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Cargar listado de productos desde el backend una sola vez
    LaunchedEffect(Unit) {
        try {
            listaProductos = productosRepo.obtenerProductos()
            errorMsg = null
        } catch (e: Exception) {
            errorMsg = e.localizedMessage
        } finally {
            cargando = false
        }
    }

    // Buscar el producto concreto en la lista
    val producto: ProductoDTO? = remember(listaProductos, id) {
        listaProductos.firstOrNull { it.id?.toInt() == id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de producto",
                        color = Color(0xFF39FF14),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF39FF14)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
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
                val p = producto
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Imagen del producto
                    Image(
                        painter = rememberAsyncImagePainter(p!!.imagenUrl),
                        contentDescription = p.nombreProducto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Nombre
                    Text(
                        text = p.nombreProducto,
                        color = Color(0xFF39FF14),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Precio
                    Text(
                        text = "Precio: ${'$'}${"%.2f".format(p.precioProducto)}",
                        color = Color(0xFF1E90FF),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Descripción
                    Text(
                        "Descripción",
                        color = Color(0xFF39FF14),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        p.descripcionProducto,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Botón agregar al carrito
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                // Agregar al carrito usa el repositorio local
                                carritoVM.agregarProductoAlCarrito(
                                    idProducto = p.id ?: 0L,
                                    nombre = p.nombreProducto,
                                    precio = p.precioProducto,
                                    imagenUrl = p.imagenUrl
                                )
                            }
                        ) {
                            Text("Agregar al carrito")
                        }
                    }
                }
            }
        }
    }
}