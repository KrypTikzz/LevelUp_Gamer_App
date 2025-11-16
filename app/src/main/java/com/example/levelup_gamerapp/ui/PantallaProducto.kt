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
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProducto(
    id: Int,                // id que viene de la navegación ("producto/{id}")
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // ViewModel de productos (REMOTO)
    val productosVM: ProductosViewModel = viewModel(
        factory = ProductosViewModelFactory(RemoteProductosRepository())
    )

    // ViewModel de carrito (LOCAL, igual que antes)
    val db = AppDatabase.obtenerBaseDatos(context)
    val cDao = db.carritoDao()
    val cRepo = CarritoRepository(cDao)
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(cRepo))

    // Lista de productos desde el backend
    val listaProductos by productosVM.productos.collectAsState(initial = emptyList())

    // Buscar el producto por id dentro de la lista
    val producto: ProductoDTO? = remember(listaProductos, id) {
        listaProductos.firstOrNull { it.id?.toInt() == id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de producto",
                        color = Color(0xFF39FF14),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF39FF14)
                        )
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { padding ->

        when {
            // Aún no hay productos cargados
            listaProductos.isEmpty() && producto == null -> {
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

            // No se encontró el producto con ese id
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
                    // Imagen
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
                        p.nombreProducto,
                        color = Color(0xFF39FF14),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Precio
                    Text(
                        text = "Precio: $${"%.2f".format(p.precioProducto)}",
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
                                carritoVM.agregarProductoAlCarrito(
                                    idProducto = p.id ?: 0L,   // ID real del backend
                                    nombre = p.nombreProducto,
                                    precio = p.precioProducto,
                                    imagenUrl = p.imagenUrl
                                )
                            }
                        ) {
                            Text("🛒 Agregar al carrito")
                        }
                    }
                }
            }
        }
    }
}
