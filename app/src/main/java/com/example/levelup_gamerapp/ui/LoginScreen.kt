package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.repository.LoginRepository
import com.example.levelup_gamerapp.viewmodel.LoginViewModel
import com.example.levelup_gamerapp.viewmodel.LoginViewModelFactory
import com.example.levelup_gamerapp.viewmodel.SesionViewModel
// import androidx.compose.ui.text.input.KeyboardOptions  // ← ya no se usa

/**
 * Pantalla de inicio de sesión. Gestiona el ingreso de credenciales y
 * reacciona a los estados del [LoginViewModel] para navegar a la pantalla
 * correspondiente. También actualiza el [SesionViewModel] con la información
 * del usuario autenticado.
 *
 * @param navController controlador de navegación para movernos entre pantallas
 * @param sesionViewModel viewModel compartido para mantener el estado de sesión
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    sesionViewModel: SesionViewModel
) {
    val context = LocalContext.current
    // Obtenemos DAO y repositorio a partir de la BD
    val dao = remember { AppDatabase.getDatabase(context).registroUsuarioDao() }
    val repository = remember { LoginRepository(dao) }
    val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(repository))

    // Estados locales de los campos
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Observamos los estados del ViewModel
    val mensaje by vm.mensaje.collectAsState()
    val loginExitoso by vm.loginExitoso.collectAsState()
    val esAdmin by vm.esAdmin.collectAsState()

    // Cuando el login sea exitoso, actualizamos la sesión y navegamos
    LaunchedEffect(loginExitoso) {
        if (loginExitoso) {
            sesionViewModel.login(correo.trim(), esAdmin)
            vm.resetEstado()
            if (esAdmin) {
                navController.navigate("admin") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                navController.navigate("productos") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "LEVEL-UP GAMER",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF39FF14),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo correo
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón login
            Button(
                onClick = { vm.iniciarSesion(correo.trim(), contrasena) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            // Mostrar mensaje si existe
            if (mensaje.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mensaje,
                    color = if (mensaje.contains("exitoso", ignoreCase = true))
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace a registro
            TextButton(onClick = { navController.navigate("registro") }) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}
