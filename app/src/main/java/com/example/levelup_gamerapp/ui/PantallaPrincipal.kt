package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.RemoteProductosRepository

@Composable
fun PantallaPrincipal(navController: NavController) {
    val productosRepo = remember { RemoteProductosRepository() }

    var productos by remember { mutableStateOf<List<ProductoDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            productos = productosRepo.obtenerProductos()
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
                    DestacadosSection(
                        productos = productos.take(6),
                        onProductoClick = { productoId ->
                            // ✅ SIN toInt(): el backend usa Long
                            navController.navigate("producto/$productoId")
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
            painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1511512578047-dfb367046420"),
            contentDescription = "Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Bienvenido a LevelUp Gamer",
                color = Color(0xFF39FF14),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "Ofertas y productos destacados",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DestacadosSection(
    productos: List<ProductoDTO>,
    onProductoClick: (Long) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Destacados",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        productos.forEach { p ->
            val id = p.id ?: return@forEach

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(p.imagenUrl),
                        contentDescription = p.nombreProducto,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.nombreProducto, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("$${p.precioProducto}", color = Color(0xFF39FF14))
                    }

                    Button(
                        onClick = { onProductoClick(id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF39FF14),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Ver")
                    }
                }
            }
        }
    }
}

@Composable
fun FooterSeccion() {
    Spacer(modifier = Modifier.height(18.dp))
    Text(
        text = "© LevelUp Gamer",
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    Spacer(modifier = Modifier.height(24.dp))
}
