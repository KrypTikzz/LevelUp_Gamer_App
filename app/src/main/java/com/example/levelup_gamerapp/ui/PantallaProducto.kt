package com.example.levelup_gamerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.local.AppDatabase
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.CarritoRepository
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import com.example.levelup_gamerapp.viewmodel.CarritoViewModel
import com.example.levelup_gamerapp.viewmodel.CarritoViewModelFactory

// --------------------
// MODELO COMENTARIO UI
// --------------------
data class ComentarioUI(
    val usuario: String,
    val comentario: String,
    val rating: Int
)

// --------------------
// COMPONENTE CARITAS (1 a 5)
// --------------------
@Composable
fun RatingCaritas(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row {
        for (i in 1..5) {
            Text(
                text = if (i <= rating) "😄" else "🙂",
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onRatingChange(i) }
            )
        }
    }
}

/**
 * Pantalla de detalle de un producto.
 * Incluye comentarios y calificación (mock frontend).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProducto(
    id: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Carrito local (Room)
    val db = remember { AppDatabase.getDatabase(context) }
    val carritoRepo = remember { CarritoRepository(db.carritoDao()) }
    val carritoViewModel: CarritoViewModel =
        viewModel(factory = CarritoViewModelFactory(carritoRepo))

    // Repositorio remoto
    val productosRepo = remember { RemoteProductosRepository() }

    var producto by remember { mutableStateOf<ProductoDTO?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // --------------------
    // ESTADOS COMENTARIOS
    // --------------------
    var nombreUsuario by remember { mutableStateOf("Usuario") }
    var rating by remember { mutableStateOf(0) }
    var comentarioTexto by remember { mutableStateOf("") }

    val comentarios = remember {
        mutableStateListOf(
            ComentarioUI("Juan", "Muy buen producto", 5),
            ComentarioUI("Ana", "Cumple su función", 3)
        )
    }

    // Cargar producto
    LaunchedEffect(id) {
        cargando = true
        errorMsg = null
        producto = null
        try {
            producto = productosRepo.obtenerProducto(id)
        } catch (e: Exception) {
            errorMsg = e.localizedMessage ?: "Error al cargar producto"
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de producto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        }
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

            errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(errorMsg ?: "Error desconocido", color = Color.White)
                }
            }

            producto == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Producto no encontrado", color = Color.White)
                }
            }

            else -> {
                val p = producto!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.Black)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Imagen
                    Image(
                        painter = rememberAsyncImagePainter(p.imagenUrl),
                        contentDescription = p.nombreProducto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = p.nombreProducto,
                        color = Color(0xFF39FF14),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$${p.precioProducto}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(p.descripcionProducto, color = Color.White)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Stock disponible: ${p.cantidadDisponible}",
                        color = Color(0xFF1E90FF)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            carritoViewModel.agregarProductoAlCarrito(
                                idProducto = p.id ?: id,
                                nombre = p.nombreProducto,
                                precio = p.precioProducto,
                                imagenUrl = p.imagenUrl
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = p.cantidadDisponible > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF39FF14),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(if (p.cantidadDisponible > 0) "Agregar al carrito" else "Sin stock")
                    }

                    // ======================
                    // COMENTARIOS
                    // ======================
                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "Comentarios",
                        color = Color(0xFF39FF14),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = { nombreUsuario = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RatingCaritas(
                        rating = rating,
                        onRatingChange = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comentarioTexto,
                        onValueChange = { comentarioTexto = it },
                        label = { Text("Escribe tu comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (comentarioTexto.isNotBlank() && rating > 0) {
                                comentarios.add(
                                    ComentarioUI(
                                        usuario = nombreUsuario.ifBlank { "Usuario" },
                                        comentario = comentarioTexto,
                                        rating = rating
                                    )
                                )
                                comentarioTexto = ""
                                rating = 0
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF39FF14),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Publicar comentario")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    comentarios.forEach { c ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${c.usuario}  ${"😄".repeat(c.rating)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(c.comentario, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
