package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.levelup_gamerapp.remote.CategoriaDTO
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.repository.RemoteCategoriasRepository
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import com.example.levelup_gamerapp.repository.RemoteUsuariosRepository
import com.example.levelup_gamerapp.viewmodel.CategoriasViewModel
import com.example.levelup_gamerapp.viewmodel.CategoriasViewModelFactory
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory
import com.example.levelup_gamerapp.viewmodel.UsuariosViewModel
import com.example.levelup_gamerapp.viewmodel.UsuariosViewModelFactory

/**
 * Pantalla de administración que gestiona productos, categorías y usuarios
 * directamente desde el backend. Se organizan tres pestañas donde cada
 * sección permite listar y crear entidades remotas. Al migrar completamente
 * al backend se evita mantener datos en la base local salvo el carrito.
 */
@Composable
fun PantallaAdmin(navController: NavController) {
    val context = LocalContext.current

    // Repositorios remotos
    val productosRepo = remember { RemoteProductosRepository() }
    val categoriasRepo = remember { RemoteCategoriasRepository() }
    val usuariosRepo = remember { RemoteUsuariosRepository() }

    // ViewModels remotos
    val productosVM: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(productosRepo))
    val categoriasVM: CategoriasViewModel = viewModel(factory = CategoriasViewModelFactory(categoriasRepo))
    val usuariosVM: UsuariosViewModel = viewModel(factory = UsuariosViewModelFactory(usuariosRepo))

    // Estados de las listas
    val productos by productosVM.productos.collectAsState()
    val categorias by categoriasVM.categorias.collectAsState()
    val usuarios by usuariosVM.usuarios.collectAsState()

    // Cargar datos al componer la pantalla. Solo se ejecuta una vez.
    LaunchedEffect(Unit) {
        productosVM.cargarProductos()
        categoriasVM.cargarCategorias()
        usuariosVM.cargarUsuarios()
    }

    // Control de pestañas: 0 -> Productos, 1 -> Categorías, 2 -> Usuarios
    var selectedTabIndex by remember { mutableStateOf(0) }

    // ---- Estados del formulario de productos ----
    var nombreProducto by remember { mutableStateOf("") }
    var descripcionProducto by remember { mutableStateOf("") }
    var precioProducto by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var stockProducto by remember { mutableStateOf("") }
    // Índice seleccionado en el listado de categorías para el producto
    var categoriaProductoIndex by remember { mutableStateOf(0) }

    // ---- Estados del formulario de categorías ----
    var nombreCategoria by remember { mutableStateOf("") }
    var descripcionCategoria by remember { mutableStateOf("") }
    // 🆕 id de la categoría que se está editando; null -> crear
    var categoriaEditandoId by remember { mutableStateOf<Long?>(null) }

    // ---- Estados del formulario de usuarios ----
    var nombreUsuario by remember { mutableStateOf("") }
    var apellidoUsuario by remember { mutableStateOf("") }
    var correoUsuario by remember { mutableStateOf("") }
    var contrasenaUsuario by remember { mutableStateOf("") }
    var edadUsuario by remember { mutableStateOf("") }
    var adminUsuario by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
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
                text = { Text("Categorías") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("Usuarios") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            // === TAB PRODUCTOS ===
            0 -> {
                Column(modifier = Modifier.fillMaxSize()) {

                    // 🔹 Formulario de creación de productos (mitad superior, con scroll)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Formulario de creación de productos
                        Text("Nuevo producto", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nombreProducto,
                            onValueChange = { nombreProducto = it },
                            label = { Text("Nombre del producto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descripcionProducto,
                            onValueChange = { descripcionProducto = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = precioProducto,
                            onValueChange = { precioProducto = it },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = imagenUrl,
                            onValueChange = { imagenUrl = it },
                            label = { Text("URL de imagen") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = stockProducto,
                            onValueChange = { stockProducto = it },
                            label = { Text("Stock disponible") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Selección de categoría para el producto
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Categoría", style = MaterialTheme.typography.bodyLarge)
                        if (categorias.isEmpty()) {
                            Text(
                                "(No hay categorías disponibles)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            // Mostramos un menú desplegable con las categorías existentes. El primer
                            // elemento es seleccionado por defecto y se controla con el índice.
                            var expandedCat by remember { mutableStateOf(false) }
                            val categoriaSeleccionada = categorias.getOrNull(categoriaProductoIndex)
                            Box {
                                OutlinedTextField(
                                    value = categoriaSeleccionada?.nombreCategoria ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Seleccione categoría") },
                                    trailingIcon = {
                                        IconButton(onClick = { expandedCat = !expandedCat }) {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowDropDown,
                                                contentDescription = "Categorías"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropdownMenu(
                                    expanded = expandedCat,
                                    onDismissRequest = { expandedCat = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    categorias.forEachIndexed { index, cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.nombreCategoria) },
                                            onClick = {
                                                categoriaProductoIndex = index
                                                expandedCat = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val precio = precioProducto.toDoubleOrNull()
                                val stock = stockProducto.toIntOrNull()
                                val categoria = categorias.getOrNull(categoriaProductoIndex)
                                if (nombreProducto.isNotBlank() && descripcionProducto.isNotBlank() &&
                                    precio != null && stock != null && categoria != null
                                ) {
                                    val nuevoProd = ProductoDTO(
                                        nombreProducto = nombreProducto.trim(),
                                        descripcionProducto = descripcionProducto.trim(),
                                        precioProducto = precio,
                                        imagenUrl = imagenUrl.ifBlank { "https://via.placeholder.com/300" },
                                        cantidadDisponible = stock,
                                        categoriaId = categoria.id ?: 0L,
                                        categoriaProducto = categoria.nombreCategoria
                                    )
                                    productosVM.agregarProducto(nuevoProd)
                                    // Limpiar campos del formulario
                                    nombreProducto = ""
                                    descripcionProducto = ""
                                    precioProducto = ""
                                    imagenUrl = ""
                                    stockProducto = ""
                                    categoriaProductoIndex = 0
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Agregar producto")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Productos actuales", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Lista de productos (mitad inferior)
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(productos) { prod ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            prod.nombreProducto,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            "$${prod.precioProducto}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text("Stock: ${prod.cantidadDisponible}")
                                        Text("Categoría: ${prod.categoriaProducto}")
                                    }
                                    // Botones de acción: Editar y Eliminar
                                    Column(horizontalAlignment = Alignment.End) {
                                        TextButton(
                                            onClick = {
                                                prod.id?.let { id ->
                                                    // Navegamos a la pantalla de edición de producto
                                                    navController.navigate("editar_producto/$id")
                                                }
                                            }
                                        ) {
                                            Text("Editar")
                                        }
                                        TextButton(
                                            onClick = {
                                                prod.id?.let { id ->
                                                    productosVM.eliminarProducto(id)
                                                }
                                            }
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // === TAB CATEGORÍAS ===
            1 -> {
                Column(modifier = Modifier.fillMaxSize()) {

                    // 🔹 Formulario de categorías (mitad superior)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            if (categoriaEditandoId == null) "Nueva categoría"
                            else "Editar categoría",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nombreCategoria,
                            onValueChange = { nombreCategoria = it },
                            label = { Text("Nombre de la categoría") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descripcionCategoria,
                            onValueChange = { descripcionCategoria = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (nombreCategoria.isNotBlank()) {
                                    val dto = CategoriaDTO(
                                        id = categoriaEditandoId,
                                        nombreCategoria = nombreCategoria.trim(),
                                        descripcionCategoria = descripcionCategoria.trim()
                                    )
                                    if (categoriaEditandoId == null) {
                                        // Crear nueva categoría
                                        categoriasVM.crearCategoria(dto)
                                    } else {
                                        // Actualizar existente
                                        categoriasVM.actualizarCategoria(
                                            categoriaEditandoId!!,
                                            dto
                                        )
                                    }
                                    // Limpiar formulario
                                    nombreCategoria = ""
                                    descripcionCategoria = ""
                                    categoriaEditandoId = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (categoriaEditandoId == null)
                                    "Agregar categoría"
                                else
                                    "Guardar cambios"
                            )
                        }

                        // Botón para cancelar la edición si corresponde
                        if (categoriaEditandoId != null) {
                            TextButton(
                                onClick = {
                                    nombreCategoria = ""
                                    descripcionCategoria = ""
                                    categoriaEditandoId = null
                                }
                            ) {
                                Text("Cancelar edición")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Categorías actuales", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Lista de categorías (mitad inferior)
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(categorias) { cat ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            cat.nombreCategoria,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            cat.descripcionCategoria,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text("ID: ${cat.id ?: "-"}")
                                    }
                                    // 🆕 Botones Editar / Eliminar
                                    Column(horizontalAlignment = Alignment.End) {
                                        TextButton(
                                            onClick = {
                                                // Cargar datos en el formulario para editar
                                                categoriaEditandoId = cat.id
                                                nombreCategoria = cat.nombreCategoria
                                                descripcionCategoria = cat.descripcionCategoria
                                            }
                                        ) {
                                            Text("Editar")
                                        }
                                        TextButton(
                                            onClick = {
                                                cat.id?.let { id ->
                                                    // Si estoy editando esta misma, limpiar formulario
                                                    if (categoriaEditandoId == id) {
                                                        nombreCategoria = ""
                                                        descripcionCategoria = ""
                                                        categoriaEditandoId = null
                                                    }
                                                    categoriasVM.eliminarCategoria(id)
                                                }
                                            }
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // === TAB USUARIOS ===
            2 -> {
                Column(modifier = Modifier.fillMaxSize()) {

                    // 🔹 Formulario de usuarios (mitad superior)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Nuevo usuario", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nombreUsuario,
                            onValueChange = { nombreUsuario = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = apellidoUsuario,
                            onValueChange = { apellidoUsuario = it },
                            label = { Text("Apellido") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = correoUsuario,
                            onValueChange = { correoUsuario = it },
                            label = { Text("Correo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contrasenaUsuario,
                            onValueChange = { contrasenaUsuario = it },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = edadUsuario,
                            onValueChange = { edadUsuario = it },
                            label = { Text("Edad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Selección de rol de administrador (checkbox)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = adminUsuario,
                                onCheckedChange = { adminUsuario = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Es administrador")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val edadInt = edadUsuario.toIntOrNull()
                                if (nombreUsuario.isNotBlank() && apellidoUsuario.isNotBlank() &&
                                    correoUsuario.isNotBlank() && contrasenaUsuario.isNotBlank() && edadInt != null
                                ) {
                                    val nuevoUsuario = UsuarioDTO(
                                        nombre = nombreUsuario.trim(),
                                        apellido = apellidoUsuario.trim(),
                                        correo = correoUsuario.trim(),
                                        contrasena = contrasenaUsuario,
                                        edad = edadInt,
                                        admin = adminUsuario
                                    )
                                    usuariosVM.crearUsuario(nuevoUsuario)
                                    // Limpiar campos
                                    nombreUsuario = ""
                                    apellidoUsuario = ""
                                    correoUsuario = ""
                                    contrasenaUsuario = ""
                                    edadUsuario = ""
                                    adminUsuario = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Agregar usuario")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Usuarios actuales", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Lista de usuarios (mitad inferior)
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(usuarios) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "${user.nombre} ${user.apellido}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            user.correo,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text("Edad: ${user.edad}")
                                        Text(
                                            if (user.admin) "Admin" else "Usuario",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        // Botón para ir a la pantalla de edición de usuario
                                        TextButton(
                                            onClick = {
                                                user.id?.let { id ->
                                                    navController.navigate("editar_usuario/$id")
                                                }
                                            }
                                        ) {
                                            Text("Editar")
                                        }
                                        // Botón para eliminar usuario
                                        TextButton(
                                            onClick = {
                                                user.id?.let { id ->
                                                    usuariosVM.eliminarUsuario(id)
                                                }
                                            }
                                        ) {
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
}
