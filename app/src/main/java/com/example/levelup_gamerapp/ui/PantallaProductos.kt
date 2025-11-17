package com.example.levelup_gamerapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory

/**
 * Pantalla que muestra la grilla de productos obtenidos del backend.
 * Se ofrecen filtros por categoría y un botón para añadir cada producto al carrito
 * usando almacenamiento local.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProductos(nav: NavController) {
    val context = LocalContext.current
    val productosRepo = remember { RemoteProductosRepository() }
    var listaProductos by remember { mutableStateOf<List<ProductoDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Cargar productos una vez
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

    // Construir lista de categorías a partir de los nombres de categoría de cada producto
    val categorias = remember(listaProductos) {
        val cats = listaProductos.map { it.categoriaProducto.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
        listOf("Todos") + cats
    }

    var expanded by remember { mutableStateOf(false) }
    var categoriaSeleccionadaIndex by remember { mutableStateOf(0) }
    val categoriaSeleccionada = categorias.getOrNull(categoriaSeleccionadaIndex) ?: "Todos"

    val productosFiltrados = remember(listaProductos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todos") listaProductos
        else listaProductos.filter { it.categoriaProducto.trim() == categoriaSeleccionada }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Productos",
                        color = Color(0xFF39FF14),
                        fontWeight = FontWeight.Bold
                    )
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
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF39FF14))
                }
            }
            errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMsg ?: "Error desconocido",
                        color = Color.White
                    )
                }
            }
            listaProductos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos disponibles", color = Color.White)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black)
                ) {
                    // Selector de categoría
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = categoriaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF111111),
                                unfocusedContainerColor = Color(0xFF111111),
                                focusedLabelColor = Color(0xFF39FF14),
                                unfocusedLabelColor = Color(0xFFCCCCCC)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categorias.forEachIndexed { index, cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        categoriaSeleccionadaIndex = index
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Subtítulo
                    Text(
                        text = if (categoriaSeleccionada == "Todos")
                            "Nuestros productos"
                        else
                            "Categoría: $categoriaSeleccionada",
                        fontSize = 16.sp,
                        color = Color(0xFF1E90FF),
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                    // Grilla de productos
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productosFiltrados) { producto ->
                            ProductoCard(producto = producto) {
                                // ✅ Navegamos al detalle pasando el id numérico real
                                producto.id?.let { id ->
                                    nav.navigate("producto/$id")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de un producto individual.
 * Muestra la imagen, nombre, precio y un botón para añadir al carrito.
 */
@Composable
fun ProductoCard(producto: ProductoDTO, onClick: () -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.obtenerBaseDatos(context)
    val cRepo = remember { CarritoRepository(db.carritoDao()) }
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(cRepo))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberAsyncImagePainter(producto.imagenUrl),
                contentDescription = producto.nombreProducto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                producto.nombreProducto,
                color = Color(0xFF39FF14),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "$${"%.2f".format(producto.precioProducto)}",
                color = Color(0xFF1E90FF),
                fontSize = 12.sp
            )
            Button(
                onClick = {
                    carritoVM.agregarProductoAlCarrito(
                        idProducto = producto.id ?: 0L,
                        nombre = producto.nombreProducto,
                        precio = producto.precioProducto,
                        imagenUrl = producto.imagenUrl
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14))
            ) {
                Text(
                    "Agregar al carrito",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
