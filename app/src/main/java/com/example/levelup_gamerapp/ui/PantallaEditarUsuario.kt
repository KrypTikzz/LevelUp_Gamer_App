package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.repository.RemoteUsuariosRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarUsuario(
    navController: NavController,
    id: Long
) {
    val context = LocalContext.current
    val repo = remember { RemoteUsuariosRepository() }
    val scope = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var esAdmin by remember { mutableStateOf(false) }

    // Cargar datos del usuario al entrar
    LaunchedEffect(id) {
        try {
            val usuario = repo.obtenerUsuario(id)
            nombre = usuario.nombre
            apellido = usuario.apellido
            correo = usuario.correo
            contrasena = usuario.contrasena
            edad = usuario.edad.toString()
            esAdmin = usuario.admin
            errorMsg = null
        } catch (e: Exception) {
            errorMsg = e.localizedMessage ?: "Error al cargar usuario"
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar usuario", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF39FF14)
                    )
                }
                errorMsg != null -> {
                    Text(
                        text = errorMsg!!,
                        color = Color(0xFFFF5252),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
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
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contrasena,
                            onValueChange = { contrasena = it },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = esAdmin,
                                onCheckedChange = { esAdmin = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Es administrador", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val edadInt = edad.toIntOrNull()
                                if (nombre.isNotBlank() && apellido.isNotBlank() &&
                                    correo.isNotBlank() && contrasena.isNotBlank() &&
                                    edadInt != null
                                ) {
                                    val actualizado = UsuarioDTO(
                                        id = id,
                                        nombre = nombre.trim(),
                                        apellido = apellido.trim(),
                                        correo = correo.trim(),
                                        contrasena = contrasena,
                                        edad = edadInt,
                                        admin = esAdmin
                                    )

                                    scope.launch {
                                        try {
                                            repo.actualizarUsuario(id, actualizado)
                                            navController.popBackStack()
                                        } catch (e: Exception) {
                                            errorMsg = "Error al guardar cambios"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar cambios")
                        }
                    }
                }
            }
        }
    }
}
