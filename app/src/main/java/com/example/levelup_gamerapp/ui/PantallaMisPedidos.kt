package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamerapp.core.UserSession
import com.example.levelup_gamerapp.remote.PedidoResponseDTO
import com.example.levelup_gamerapp.repository.RemotePedidosRepository
import com.example.levelup_gamerapp.utils.generarBoletaPdf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMisPedidos(onNavigateBack: () -> Unit) {

    val context = LocalContext.current
    val repo = remember { RemotePedidosRepository() }

    val usuarioId = UserSession.idUsuario
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pedidos by remember { mutableStateOf<List<PedidoResponseDTO>>(emptyList()) }

    // Para expandir/cerrar cards
    val expanded = remember { mutableStateOf(setOf<Long>()) }

    LaunchedEffect(usuarioId) {
        if (usuarioId == null) {
            cargando = false
            error = "Debes iniciar sesión para ver tu historial."
            return@LaunchedEffect
        }

        cargando = true
        error = null
        try {
            pedidos = repo.listarPorUsuario(usuarioId)
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Error al cargar historial"
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de compras", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->

        when {
            cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF39FF14))
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error!!, color = Color(0xFFFF5252))
                }
            }

            pedidos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no tienes compras registradas.", color = Color(0xFF1E90FF))
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pedidos) { pedido ->
                        val isExpanded = expanded.value.contains(pedido.id)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Pedido #${pedido.id}",
                                            color = Color(0xFF39FF14),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Fecha: ${pedido.fechaCreacion}",
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Total: $${pedido.total}",
                                            color = Color(0xFF1E90FF),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            expanded.value = if (isExpanded) {
                                                expanded.value - pedido.id
                                            } else {
                                                expanded.value + pedido.id
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Expandir",
                                            tint = Color.White
                                        )
                                    }
                                }

                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color(0xFF2A2A2A))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    pedido.detalles.forEach { det ->
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "${det.nombreProducto} x${det.cantidad}",
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "$${det.subtotal}",
                                                color = Color(0xFFFFA500),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            generarBoletaPdf(
                                                context = context,
                                                pedido = pedido
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF39FF14),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text("Descargar boleta PDF")
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
