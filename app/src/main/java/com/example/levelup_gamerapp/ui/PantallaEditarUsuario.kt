package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.repository.RemoteUsuariosRepository
import kotlinx.coroutines.launch

/**
 * Pantalla para crear o editar un usuario desde el backend.
 * Si el parámetro [id] es 0 se considera creación, en caso contrario se
 * cargan los datos del usuario existente para su edición. Las operaciones se
 * realizan a través de [RemoteUsuariosRepository].
 */
@Composable
fun PantallaEditarUsuario(navController: NavController, id: Int) {
    val repo = remember { RemoteUsuariosRepository() }
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var usuarioRemoto by remember { mutableStateOf<UsuarioDTO?>(null) }

    // Estados de formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var admin by remember { mutableStateOf(false) }

    // Cargar usuario existente si aplica
    LaunchedEffect(id) {
        if (id != 0) {
            try {
                val usuario = repo.obtenerUsuario(id.toLong())
                usuarioRemoto = usuario
                nombre = usuario.nombre
                apellido = usuario.apellido
                correo = usuario.correo
                contrasena = usuario.contrasena
                edad = usuario.edad.toString()
                admin = usuario.admin
                errorMsg = null
            } catch (e: Exception) {
                errorMsg = e.localizedMessage
            } finally {
                cargando = false
            }
        } else {
            cargando = false
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (id == 0) "Crear Usuario" else "Editar Usuario",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Campos de entrada
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            enabled = id == 0
        )
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botón Guardar
        Button(
            onClick = {
                val edadInt = edad.toIntOrNull() ?: 0
                if (nombre.isNotBlank() && apellido.isNotBlank() && correo.isNotBlank() && contrasena.isNotBlank()) {
                    val dto = UsuarioDTO(
                        id = if (id == 0) null else id.toLong(),
                        nombre = nombre.trim(),
                        apellido = apellido.trim(),
                        correo = correo.trim(),
                        contrasena = contrasena,
                        edad = edadInt,
                        admin = admin
                    )
                    scope.launch {
                        try {
                            if (id == 0) {
                                repo.crearUsuario(dto)
                            } else {
                                repo.actualizarUsuario(id.toLong(), dto)
                            }
                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMsg = e.localizedMessage
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Botón Eliminar (solo si el usuario existe y no es admin principal)
        if (id != 0 && usuarioRemoto != null && usuarioRemoto!!.correo != "admin@levelupgamer.cl") {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            repo.eliminarUsuario(id.toLong())
                            navController.popBackStack()
                        } catch (e: Exception) {
                            errorMsg = e.localizedMessage
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Botón Cancelar
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
        // Mostrar mensaje de error si corresponde
        errorMsg?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}