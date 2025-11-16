package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.ProductosEntity
import com.example.levelup_gamerapp.repository.ProductosRepository
import com.example.levelup_gamerapp.viewmodel.ProductosViewModel
import com.example.levelup_gamerapp.viewmodel.ProductosViewModelFactory

/**
 * Pantalla para editar un producto existente. Carga el producto por su ID,
 * permite modificar sus campos y actualiza la base de datos al guardar.
 * Solo debería ser accesible por usuarios administradores.
 *
 * @param navController controlador de navegación para volver a la lista
 * @param id identificador del producto a editar
 */
@Composable
fun PantallaEditarProducto(navController: NavController, id: Int) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).productosDao() }
    val repo = remember { ProductosRepository(dao) }
    val productosViewModel: ProductosViewModel = viewModel(factory = ProductosViewModelFactory(repo))

    // Obtenemos el producto desde el ViewModel
    val producto by productosViewModel.obtenerProductoPorId(id).collectAsState(initial = null)

    // Estados para los campos, inicializados con los valores del producto cuando esté disponible
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    LaunchedEffect(producto) {
        producto?.let { prod ->
            nombre = prod.nombre
            descripcion = prod.descripcion
            precio = prod.precio.toString()
            imagenUrl = prod.imagenUrl
            categoria = prod.categoria
            stock = prod.cantidadDisponible.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Editar Producto",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
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
            label = { Text("URL de imagen") },
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

        Spacer(modifier = Modifier.padding(8.dp))

        Button(onClick = {
            // Validaciones mínimas
            val precioDouble = precio.toDoubleOrNull()
            val stockInt = stock.toIntOrNull()
            if (precioDouble != null && stockInt != null && producto != null) {
                val actualizado = ProductosEntity(
                    id = producto!!.id,
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim(),
                    precio = precioDouble,
                    imagenUrl = imagenUrl.ifBlank { "https://via.placeholder.com/300" },
                    categoria = categoria.ifBlank { "General" },
                    cantidadDisponible = stockInt
                )
                productosViewModel.agregarProducto(actualizado)
                navController.popBackStack()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }
}