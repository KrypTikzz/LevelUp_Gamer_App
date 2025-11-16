package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.levelup_gamerapp.viewmodel.SesionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    sesionViewModel: SesionViewModel,
    onNavigate: (String) -> Unit
) {
    val isLoggedIn by sesionViewModel.isLoggedIn.collectAsState()
    val esAdmin by sesionViewModel.esAdmin.collectAsState()

    // 🎨 Snackbar del drawer
    SnackbarHost(hostState = snackbarHostState) { data ->
        val bgColor = when {
            data.visuals.message.contains("cerrada") -> Color(0xFF00C853) // éxito
            data.visuals.message.contains("no hay", ignoreCase = true) -> Color(0xFFD32F2F) // error
            else -> Color.DarkGray
        }
        Snackbar(
            snackbarData = data,
            containerColor = bgColor,
            contentColor = Color.White
        )
    }

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF0A0A0A),
        drawerContentColor = Color.White
    ) {
        RowTopClose(scope, drawerState)

        Text(
            text = "LEVEL-UP GAMER",
            color = Color(0xFF39FF14),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        // 🟢 Opciones principales
        DrawerItem("Inicio", "inicio", Color(0xFF1E90FF), onNavigate)
        DrawerItem("Productos", "productos", Color(0xFF39FF14), onNavigate)
        DrawerItem("Novedades", "novedades", Color(0xFF1E90FF), onNavigate)
        DrawerItem("Contacto", "contacto", Color(0xFFFFA500), onNavigate)
        DrawerItem("Carrito", "carrito", Color(0xFFFFA500), onNavigate)

        // Admin
        if (isLoggedIn && esAdmin) {
            DrawerItem("Administrador", "admin", Color(0xFFE91E63), onNavigate)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider(color = Color(0xFF222222), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Login / logout
        if (!isLoggedIn) {
            DrawerItem("Login", "login", Color(0xFF1E90FF), onNavigate)
            DrawerItem("Registro", "registro", Color(0xFFE91E63), onNavigate)
        } else {
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    scope.launch {
                        sesionViewModel.logout()
                        drawerState.close()
                        onNavigate("login")
                        snackbarHostState.showSnackbar("✅ Sesión cerrada correctamente")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E90FF),
                    contentColor = Color.White
                )
            ) {
                Text("Cerrar sesión", color = Color.White)
            }
        }
    }
}

@Composable
private fun RowTopClose(scope: CoroutineScope, drawerState: DrawerState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { scope.launch { drawerState.close() } }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar menú",
                tint = Color(0xFF39FF14)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    title: String,
    route: String,
    color: Color,
    onNavigate: (String) -> Unit
) {
    NavigationDrawerItem(
        label = { Text(title, color = color) },
        selected = false,
        onClick = { onNavigate(route) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
