package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.ProductosEntity
import com.example.levelup_gamerapp.repository.ProductosRepository
import com.example.levelup_gamerapp.repository.RegistroUsuarioRepository
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory
import com.example.levelup_gamerapp.viewmodel.UsuariosViewModel
import com.example.levelup_gamerapp.viewmodel.UsuariosViewModelFactory
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

/**
 * Vista de administración para gestionar productos. Permite añadir nuevos
 * productos, listar los existentes, eliminarlos y navegar a una pantalla de
 * edición para modificar sus datos.
 */
@Composable
fun PantallaAdmin(navController: NavController) {
    val context = LocalContext.current

    // ViewModel para productos
    val productosDao = remember { AppDatabase.getDatabase(context).productosDao() }
    val productosRepo = remember { ProductosRepository(productosDao) }
    val productosViewModel: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(productosRepo))
    val productos by productosViewModel.productos.collectAsState()

    // ViewModel para usuarios
    val usuariosDao = remember { AppDatabase.getDatabase(context).registroUsuarioDao() }
    val usuariosRepo = remember { RegistroUsuarioRepository(usuariosDao) }
    val usuariosViewModel: UsuariosViewModel = viewModel(factory = UsuariosViewModelFactory(usuariosRepo))
    val usuarios by usuariosViewModel.usuarios.collectAsState()

    // Tabs: 0 -> Productos, 1 -> Usuarios
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Campos para nuevo producto
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Cargar productos la primera vez
    LaunchedEffect(Unit) {
        productosViewModel.cargarProductos()
    }

    val scrollForm = rememberScrollState() // scroll para el formulario

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding() // evita que el teclado tape contenido
    ) {
        Text("Panel Administrador", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Barra de pestañas
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Productos") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Usuarios") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            0 -> {
                // ----------- Mitad superior: FORMULARIO (scrollable) -----------
                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollForm)
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del producto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = imagenUrl,
                            onValueChange = { imagenUrl = it },
                            label = { Text("URL de imagen (opcional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = { categoria = it },
                            label = { Text("Categoría") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            label = { Text("Stock disponible") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val precioDouble = precio.toDoubleOrNull()
                                val stockInt = stock.toIntOrNull()
                                if (nombre.isNotBlank() && precioDouble != null && stockInt != null) {
                                    val nuevo = ProductosEntity(
                                        nombre = nombre.trim(),
                                        descripcion = descripcion.trim(),
                                        precio = precioDouble,
                                        imagenUrl = imagenUrl.ifBlank { "https://via.placeholder.com/300" },
                                        categoria = categoria.ifBlank { "General" },
                                        cantidadDisponible = stockInt
                                    )
                                    productosViewModel.agregarProducto(nuevo)
                                    // Reiniciar campos
                                    nombre = ""
                                    descripcion = ""
                                    precio = ""
                                    imagenUrl = ""
                                    categoria = ""
                                    stock = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Agregar producto")
                        }
                    }
                }

                // Separador entre mitades
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // ----------- Mitad inferior: LISTADO (ocupa todo y scrollea) -----------
                Box(modifier = Modifier.weight(1f)) {
                    Column(Modifier.fillMaxSize()) {
                        Text("Productos actuales", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(productos) { prod ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(prod.nombre, style = MaterialTheme.typography.bodyLarge)
                                            Text("$${prod.precio}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Stock: ${prod.cantidadDisponible}")
                                        }
                                        TextButton(onClick = { navController.navigate("editar_producto/${prod.id}") }) {
                                            Text("Editar")
                                        }
                                        TextButton(onClick = { productosViewModel.eliminarProducto(prod) }) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Sección de gestión de usuarios (igual que antes)
                Button(onClick = { navController.navigate("editar_usuario/0") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear usuario")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Usuarios actuales", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(usuarios) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${user.nombre} ${user.apellido}", style = MaterialTheme.typography.bodyLarge)
                                    Text(user.correo, style = MaterialTheme.typography.bodyMedium)
                                    Text("Edad: ${user.edad}", style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { navController.navigate("editar_usuario/${user.id}") }) {
                                    Text("Editar")
                                }
                                // Evitar que el admin se elimine a sí mismo
                                if (user.correo != "admin@levelupgamer.cl") {
                                    TextButton(onClick = { usuariosViewModel.eliminarUsuario(user.id) }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
