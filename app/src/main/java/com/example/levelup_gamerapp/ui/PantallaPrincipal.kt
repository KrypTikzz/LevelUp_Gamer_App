package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.RemoteProductosRepository

@Composable
fun PantallaPrincipal(navController: NavHostController) {
    val productosRepo = remember { RemoteProductosRepository() }
    var listaProductos by remember { mutableStateOf<List<ProductoDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Cargar productos destacados desde el backend al iniciar
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

    when {
        cargando -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMsg ?: "Error al cargar productos",
                    color = Color.White
                )
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                item { BannerPrincipal() }
                item {
                    ProductosDestacados(
                        productos = listaProductos,
                        onProductoClick = { productoId ->
                            // Navegamos al detalle; el NavGraph espera un Int
                            navController.navigate("producto/${productoId.toInt()}")
                        }
                    )
                }
                item { FooterSeccion() }
            }
        }
    }
}

@Composable
fun BannerPrincipal() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                "https://www.azernews.az/media/2023/11/27/2023_rog_zephyrus_duo_16_gx650_scenario_photo_01.jpg?v=1701092248"
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(0xAA000000))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "PRODUCTOS DESTACADOS",
                color = Color(0xFF39FF14),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Lo más vendido esta semana",
                color = Color(0xFF1E90FF),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProductosDestacados(
    productos: List<ProductoDTO>,
    onProductoClick: (Long) -> Unit
) {
    // Tomamos los primeros 4 productos como destacados
    val destacados = productos.take(4)

    Text(
        "Destacados",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(16.dp)
    )

    if (destacados.isEmpty()) {
        Text(
            text = "No hay productos disponibles",
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(destacados) { producto ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            producto.id?.let { onProductoClick(it) }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(producto.imagenUrl),
                            contentDescription = producto.nombreProducto,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            producto.nombreProducto,
                            color = Color(0xFF39FF14),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Precio: $${"%.2f".format(producto.precioProducto)}",
                            color = Color(0xFF1E90FF),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FooterSeccion() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "© 2025 LEVEL-UP GAMER. Todos los derechos reservados.",
            color = Color(0xFF1E90FF),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
