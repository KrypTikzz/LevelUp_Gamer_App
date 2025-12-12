package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelup_gamerapp.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

/**
 * Punto central de navegación de la aplicación. Gestiona el Drawer, la barra superior y el grafo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sesionViewModel: SesionViewModel = viewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                scope = scope,
                drawerState = drawerState,
                snackbarHostState = snackbarHostState,
                sesionViewModel = sesionViewModel,
                onNavigate = { route ->
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color(0xFF39FF14)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "LevelUp Gamer",
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black
                    )
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Black
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                innerPadding = innerPadding,
                sesionViewModel = sesionViewModel
            )
        }
    }
}

@Composable
private fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    sesionViewModel: SesionViewModel
) {
    val isLoggedIn by sesionViewModel.isLoggedIn.collectAsState()
    val esAdmin by sesionViewModel.esAdmin.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "productos" else "login",
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.Black)
    ) {
        composable("login") {
            LoginScreen(navController = navController, sesionViewModel = sesionViewModel)
        }
        composable("registro") {
            RegistroUsuarioScreen(navController = navController)
        }
        composable("inicio") {
            PantallaPrincipal(navController)
        }
        composable("productos") {
            PantallaProductos(navController)
        }

        // ✅ Long ID
        composable("producto/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            if (id != null) {
                PantallaProducto(
                    id = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                PlaceholderScreen("Error: producto no encontrado")
            }
        }

        composable("novedades") { PantallaNovedades() }
        composable("contacto") { PantallaContacto() }
        composable("carrito") { PantallaCarrito() }

        // ✅ Historial compras (cliente)
        composable("mis_pedidos") {
            if (isLoggedIn && !esAdmin) {
                PantallaMisPedidos(onNavigateBack = { navController.popBackStack() })
            } else {
                PlaceholderScreen("Acceso restringido")
            }
        }

        // Admin
        composable("admin") {
            if (esAdmin) {
                PantallaAdmin(navController)
            } else {
                PlaceholderScreen("Acceso restringido")
            }
        }

        // ✅ Long ID
        composable("editar_producto/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            if (id != null && esAdmin) {
                PantallaEditarProducto(navController, id)
            } else {
                PlaceholderScreen("Acceso restringido")
            }
        }

        // ✅ Long ID
        composable("editar_usuario/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            if (esAdmin) {
                PantallaEditarUsuario(navController, id)
            } else {
                PlaceholderScreen("Acceso restringido")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(texto: String) {
    Surface(color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(texto, color = Color(0xFF1E90FF))
        }
    }
}
