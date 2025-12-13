package com.example.levelup_gamerapp.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelup_gamerapp.core.UserSession
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.CrearPedidoRequest
import com.example.levelup_gamerapp.remote.ItemPedidoRequest
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCheckout(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Carrito (Room)
    val db = AppDatabase.obtenerBaseDatos(context)
    val repo = CarritoRepository(db.carritoDao())
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(repo))

    val carrito by carritoVM.carrito.collectAsState(initial = emptyList())

    // Agrupar por producto y sumar cantidades
    val carritoAgrupado = remember(carrito) {
        carrito
            .groupBy { it.idProducto }
            .map { (_, items) ->
                val primero = items.first()
                val cantidadTotal = items.sumOf { it.cantidad }
                primero.copy(cantidad = cantidadTotal)
            }
    }

    val totalLocal = carritoAgrupado.sumOf { it.precio * it.cantidad }

    var direccion by remember { mutableStateOf("") }
    var tarjeta by remember { mutableStateOf("") }          // 16 dígitos
    var vencimiento by remember { mutableStateOf("") }      // MM/YY
    var cvc by remember { mutableStateOf("") }              // 3 dígitos

    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checkout",
                        color = Color(0xFF39FF14),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF111111),
                    contentColor = Color.White,
                    actionColor = Color(0xFF39FF14)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Total a pagar: $${"%.2f".format(totalLocal)}",
                color = Color(0xFF1E90FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección de envío (Referencial)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = tarjeta,
                onValueChange = {
                    // Solo números, máximo 16
                    tarjeta = it.filter { ch -> ch.isDigit() }.take(16)
                },
                label = { Text("Tarjeta (Simulado)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("**** **** **** 1234") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vencimiento,
                    onValueChange = { input ->
                        vencimiento = formatVencimiento(input)
                    },
                    label = { Text("Venc. (MM/YY)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("12/27") }
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = cvc,
                    onValueChange = {
                        cvc = it.filter { ch -> ch.isDigit() }.take(3)
                    },
                    label = { Text("CVC") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("123") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMsg != null) {
                Text(errorMsg!!, color = Color(0xFFFF3B3B))
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        if (carritoAgrupado.isEmpty()) {
                            snackbarHostState.showSnackbar("No hay productos en el carrito")
                            return@launch
                        }

                        val usuarioId = UserSession.idUsuario
                        if (usuarioId == null) {
                            snackbarHostState.showSnackbar("Debes iniciar sesión para comprar")
                            return@launch
                        }

                        // Validaciones “simuladas”
                        val validacion = validarCheckoutSimulado(
                            direccion = direccion,
                            tarjeta = tarjeta,
                            vencimiento = vencimiento,
                            cvc = cvc
                        )

                        if (validacion != null) {
                            errorMsg = validacion
                            return@launch
                        }

                        errorMsg = null
                        loading = true

                        val itemsDto = carritoAgrupado.map { item ->
                            ItemPedidoRequest(
                                productoId = item.idProducto,
                                cantidad = item.cantidad
                            )
                        }

                        val request = CrearPedidoRequest(
                            usuarioId = usuarioId,
                            items = itemsDto
                        )

                        try {
                            val response = ApiClient.levelUpApi.crearPedido(request)

                            if (response.isSuccessful) {
                                val pedidoCreado = response.body()
                                if (pedidoCreado != null) {
                                    carritoVM.vaciarCarrito()

                                    val totalServidor = pedidoCreado.total
                                    val orderId = "LVL-${pedidoCreado.id}"

                                    val route =
                                        "compra_exitosa?total=${totalServidor}&orderId=${Uri.encode(orderId)}"

                                    navController.navigate(route) {
                                        popUpTo("checkout") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("compra_fallida") {
                                        popUpTo("checkout") { inclusive = true }
                                    }
                                }
                            } else {
                                navController.navigate("compra_fallida") {
                                    popUpTo("checkout") { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            navController.navigate("compra_fallida") {
                                popUpTo("checkout") { inclusive = true }
                            }
                        } finally {
                            loading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14))
            ) {
                Text(
                    text = if (loading) "Procesando..." else "Pagar y Finalizar",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nota: Datos de pago son referenciales (simulado), como en la web.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

/** Formatea en vivo el vencimiento a MM/YY (solo números). */
private fun formatVencimiento(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4) // MMYY
    return when (digits.length) {
        0, 1, 2 -> digits
        else -> digits.substring(0, 2) + "/" + digits.substring(2)
    }
}

/**
 * Retorna null si está OK. Si hay un problema, retorna el mensaje a mostrar.
 * Esto es simulación: el backend NO recibe estos campos.
 */
private fun validarCheckoutSimulado(
    direccion: String,
    tarjeta: String,
    vencimiento: String,
    cvc: String
): String? {
    if (direccion.isBlank()) return "Ingresa una dirección de envío (referencial)."

    if (tarjeta.length < 12) return "Ingresa una tarjeta válida (simulado)."

    // vencimiento MM/YY
    val parts = vencimiento.split("/")
    if (parts.size != 2 || parts[0].length != 2 || parts[1].length != 2) {
        return "Ingresa vencimiento en formato MM/YY."
    }

    val mm = parts[0].toIntOrNull() ?: return "Mes de vencimiento inválido."
    val yy = parts[1].toIntOrNull() ?: return "Año de vencimiento inválido."
    if (mm !in 1..12) return "Mes de vencimiento inválido."

    // Validación simple: no permitir fechas claramente pasadas
    val cal = Calendar.getInstance()
    val currentYY = cal.get(Calendar.YEAR) % 100
    val currentMM = cal.get(Calendar.MONTH) + 1

    if (yy < currentYY || (yy == currentYY && mm < currentMM)) {
        return "La tarjeta está vencida (simulado)."
    }

    if (cvc.length != 3) return "CVC inválido (debe tener 3 dígitos)."

    return null
}
