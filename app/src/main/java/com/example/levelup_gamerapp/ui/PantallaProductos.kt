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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.ProductosEntity
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.repository.ProductosRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProductos(nav: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = AppDatabase.obtenerBaseDatos(context).productosDao()
    val repository = ProductosRepository(dao)
    val viewModel: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(repository))
    val listaProductos by viewModel.productos.collectAsState()

    // categorías únicas + "Todos"
    val categorias = remember(listaProductos) {
        listOf("Todos") + listaProductos.map { it.categoria.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }

    // estado del selector
    var expanded by remember { mutableStateOf(false) }
    var categoriaSeleccionadaIndex by remember { mutableStateOf(0) }
    val categoriaSeleccionada = categorias.getOrNull(categoriaSeleccionadaIndex) ?: "Todos"

    // filtrado por categoría
    val productosFiltrados = remember(listaProductos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todos") listaProductos
        else listaProductos.filter { it.categoria.trim() == categoriaSeleccionada }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            if (listaProductos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos disponibles", color = Color.White)
                }
            } else {
                // ---------- Selector desplegable de categoría ----------
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

                // subtítulo
                Text(
                    text = if (categoriaSeleccionada == "Todos") "Nuestros productos"
                    else "Categoría: $categoriaSeleccionada",
                    fontSize = 16.sp,
                    color = Color(0xFF1E90FF),
                    modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                )

                // grilla de productos
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { nav.navigate("producto/${producto.id}") }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🔹 Tarjeta de producto con botón para añadir al carrito.
 */
@Composable
fun ProductoCard(producto: ProductosEntity, onClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = AppDatabase.obtenerBaseDatos(context).carritoDao()
    val repo = CarritoRepository(dao)
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(repo))

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
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(producto.nombre, color = Color(0xFF39FF14), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("$${producto.precio}", color = Color(0xFF1E90FF), fontSize = 12.sp)

            Button(
                onClick = {
                    carritoVM.agregarProductoAlCarrito(producto.nombre, producto.precio, producto.imagenUrl)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14))
            ) {
                Text("🛒 Agregar al carrito", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
