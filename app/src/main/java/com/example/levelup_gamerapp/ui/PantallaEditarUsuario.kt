package com.example.levelup_gamerapp.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.repository.RegistroUsuarioRepository
import com.example.levelup_gamerapp.viewmodel.UsuariosViewModel
import com.example.levelup_gamerapp.viewmodel.UsuariosViewModelFactory

/**
 * Pantalla para crear o editar un usuario. Si el id proporcionado es 0 se
 * interpretará como creación de un nuevo usuario; de lo contrario, se
 * precargarán los datos del usuario existente y se podrá modificarlos.
 *
 * @param navController controlador de navegación para volver a la lista
 * @param id identificador del usuario a editar (0 para crear uno nuevo)
 */
@Composable
fun PantallaEditarUsuario(navController: NavController, id: Int) {
    val context = LocalContext.current
    val usuarioDao = remember { AppDatabase.getDatabase(context).registroUsuarioDao() }
    val repo = remember { RegistroUsuarioRepository(usuarioDao) }
    val usuariosViewModel: UsuariosViewModel = viewModel(factory = UsuariosViewModelFactory(repo))

    // Obtenemos el usuario a editar, salvo que id sea 0 (nuevo)
    val usuario by usuariosViewModel.obtenerUsuarioPorId(id).collectAsState(initial = null)

    // Estados para los campos, inicializados con los valores del usuario cuando esté disponible
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var descuentoAplicado by remember { mutableStateOf("0") }
    var fotoPerfilBytes by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(usuario) {
        if (usuario != null && id != 0) {
            nombre = usuario!!.nombre
            apellido = usuario!!.apellido
            correo = usuario!!.correo
            contrasena = usuario!!.contrasena
            edad = usuario!!.edad.toString()
            descuentoAplicado = usuario!!.descuentoAplicado.toString()
            fotoPerfilBytes = usuario!!.fotoPerfil
        }
    }

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
            enabled = id == 0 // no permitimos modificar correo en edición
        )
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            singleLine = true
        )
        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descuentoAplicado,
            onValueChange = { descuentoAplicado = it },
            label = { Text("Descuento aplicado") },
            modifier = Modifier.fillMaxWidth()
        )

        fotoPerfilBytes?.let { bytes ->
            Spacer(modifier = Modifier.height(8.dp))
            // Mostrar la foto de perfil si existe (simple vista previa)
            val bitmap = remember(bytes) { android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Guardar
        Button(onClick = {
            val edadInt = edad.toIntOrNull() ?: 0
            val descuentoInt = descuentoAplicado.toIntOrNull() ?: 0
            if (nombre.isNotBlank() && apellido.isNotBlank() && correo.isNotBlank() && contrasena.isNotBlank()) {
                val usuarioEntity = RegistroUsuarioEntity(
                    id = if (id == 0) 0 else usuario!!.id,
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    correo = correo.trim(),
                    contrasena = contrasena,
                    edad = edadInt,
                    descuentoAplicado = descuentoInt,
                    fotoPerfil = fotoPerfilBytes
                )
                if (id == 0) {
                    usuariosViewModel.registrarUsuario(usuarioEntity)
                } else {
                    usuariosViewModel.actualizarUsuario(usuarioEntity)
                }
                navController.popBackStack()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Eliminar (solo si el usuario ya existe y no es el admin principal)
        if (id != 0 && usuario != null && usuario!!.correo != "admin@levelupgamer.cl") {
            Button(onClick = {
                usuariosViewModel.eliminarUsuario(usuario!!.id)
                navController.popBackStack()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botón Cancelar
        TextButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }
}