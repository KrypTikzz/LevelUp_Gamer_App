package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.repository.ProductosRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProducto(
    id: Int,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // ViewModel de productos para obtener el detalle
    val pDao = AppDatabase.obtenerBaseDatos(context).productosDao()
    val pRepo = ProductosRepository(pDao)
    val pVM: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(pRepo))

    // ViewModel de carrito
    val cDao = AppDatabase.obtenerBaseDatos(context).carritoDao()
    val cRepo = CarritoRepository(cDao)
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(cRepo))

    // Cargar producto por id desde Room
    val producto by pVM.obtenerProductoPorId(id).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de producto", color = Color(0xFF39FF14)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF39FF14))
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { padding ->
        if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("Producto no encontrado", color = Color.White)
            }
        } else {
            val p = producto!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = rememberAsyncImagePainter(p.imagenUrl),
                    contentDescription = p.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(p.nombre, color = Color(0xFF39FF14), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Categoría: ${p.categoria}", color = Color(0xFFCCCCCC), style = MaterialTheme.typography.bodyMedium)
                Text("Stock disponible: ${p.cantidadDisponible}", color = Color(0xFFCCCCCC), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Precio: $${p.precio}", color = Color(0xFF1E90FF), style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Descripción", color = Color(0xFF39FF14), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(p.descripcion, color = Color.White, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            carritoVM.agregarProductoAlCarrito(
                                idProducto = p.id.toLong(),   // 👈 ID real del producto
                                nombre = p.nombre,
                                precio = p.precio,
                                imagenUrl = p.imagenUrl
                            )
                        }
                    ) {
                        Text("🛒 Agregar al carrito")
                    }

                    // (Futuro) Simulación de compra con descuento de stock:
                    // Button(onClick = { pVM.descontarStock(p.id, 1) }) { Text("Comprar 1") }
                }
            }
        }
    }
}
