package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.ProductosEntity
import com.example.levelup_gamerapp.repository.ProductosRepository
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory

data class Producto(
    val nombre: String,
    val precio: String,
    val imagenUrl: String
)

data class Categoria(
    val nombre: String,
    val iconUrl: String
)

@Composable
fun PantallaPrincipal(navController: NavHostController) {
    // ViewModel de productos para obtener los destacados desde la base de datos
    val context = androidx.compose.ui.platform.LocalContext.current
    val productosDao = remember { AppDatabase.getDatabase(context).productosDao() }
    val repo = remember { ProductosRepository(productosDao) }
    val productosViewModel: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(repo))
    val listaProductos by productosViewModel.productos.collectAsState()

    // Cargar productos al iniciar
    LaunchedEffect(Unit) {
        productosViewModel.cargarProductos()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        item { BannerPrincipal() }
        item {
            ProductosDestacados(
                productos = listaProductos,
                onProductoClick = { productoId -> navController.navigate("producto/$productoId") }
            )
        }
        item { FooterSeccion() }
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
            Text("PRODUCTOS DESTACADOS", color = Color(0xFF39FF14), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Lo más vendido esta semana", color = Color(0xFF1E90FF), fontSize = 16.sp)
        }
    }
}

@Composable
fun ProductosDestacados(
    productos: List<ProductosEntity>,
    onProductoClick: (Int) -> Unit
) {
    // Tomamos los primeros 4 productos como destacados
    val destacados = if (productos.size > 4) productos.take(4) else productos

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
                        .clickable { onProductoClick(producto.id) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(producto.imagenUrl),
                            contentDescription = producto.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(producto.nombre, color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
                        Text("$${producto.precio}", color = Color(0xFF1E90FF), fontSize = 14.sp)
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
