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
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory

/**
 * Muestra la lista de productos en una grilla. Permite filtrar por categoría y
 * añadir un producto al carrito. Para enviar el pedido al backend se utiliza
 * el [ProductosEntity.remoteId] cuando está presente; de lo contrario se usa
 * el identificador local del producto.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProductos(nav: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Obtenemos el DAO y creamos el repositorio y el ViewModel
    // Creamos el repositorio remoto y el ViewModel asociado
    val productosRepo = RemoteProductosRepository()
    val viewModel: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(productosRepo))

    // Recuperamos la lista de productos desde el backend cuando se compone la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    // Observamos la lista de productos remotos. Se actualizará cuando se recargue desde el ViewModel.
    val listaProductos by viewModel.productos.collectAsState()

    // Calculamos las categorías únicas a partir de la lista de productos. Añadimos
    // la opción "Todos" al principio.
    val categorias = remember(listaProductos) {
        listOf("Todos") + listaProductos.map { it.categoriaProducto.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }

    // Estado del selector de categorías
    var expanded by remember { mutableStateOf(false) }
    var categoriaSeleccionadaIndex by remember { mutableStateOf(0) }
    val categoriaSeleccionada = categorias.getOrElse(categoriaSeleccionadaIndex) { "Todos" }

    // Filtramos la lista según la categoría seleccionada
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            if (listaProductos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos disponibles", color = Color.White)
                }
            } else {
                // Selector desplegable de categorías
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

                // Subtítulo con la categoría seleccionada
                Text(
                    text = if (categoriaSeleccionada == "Todos") "Nuestros productos"
                    else "Categoría: $categoriaSeleccionada",
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
                        ProductoCard(
                            producto = producto,
                            onClick = {
                                // Navegamos utilizando el id remoto del producto si existe. Si el id es nulo
                                // no navegamos a una pantalla de detalle específica.
                                producto.id?.let { id ->
                                    nav.navigate("producto/$id")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual de un producto remoto. Incluye un botón para añadir al
 * carrito. Utiliza el identificador remoto del producto para enviar
 * correctamente el pedido al backend.
 */
@Composable
fun ProductoCard(producto: ProductoDTO, onClick: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val carritoDao = AppDatabase.obtenerBaseDatos(context).carritoDao()
    val repo = CarritoRepository(carritoDao)
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
                "$${producto.precioProducto}",
                color = Color(0xFF1E90FF),
                fontSize = 12.sp
            )
            Button(
                onClick = {
                    // El ID remoto nunca debería ser nulo al listar productos. Si lo fuera, usamos 0.
                    val idParaCarrito = producto.id ?: 0L
                    carritoVM.agregarProductoAlCarrito(
                        idProducto = idParaCarrito,
                        nombre = producto.nombreProducto,
                        precio = producto.precioProducto,
                        imagenUrl = producto.imagenUrl
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14))
            ) {
                Text("Agregar al carrito", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}