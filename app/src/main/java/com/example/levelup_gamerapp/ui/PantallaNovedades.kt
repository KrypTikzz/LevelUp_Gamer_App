@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.levelup_gamerapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamerapp.remote.NoticiaMmoBombDTO
import com.example.levelup_gamerapp.repository.NoticiasRepository

@Composable
fun PantallaNovedades(navBack: () -> Unit = {}) {
    val context = LocalContext.current
    val repo = remember { NoticiasRepository() }

    var noticias by remember { mutableStateOf<List<NoticiaMmoBombDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Cargamos las noticias gamer desde la API de MMOBomb
    LaunchedEffect(Unit) {
        try {
            noticias = repo.obtenerNoticias()
            errorMsg = null
        } catch (e: Exception) {
            errorMsg = e.message ?: "Error desconocido"
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Novedades gamer",
                        color = Color(0xFF39FF14),
                        fontWeight = FontWeight.Bold
                    )
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
                        .background(Color.Black)
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF39FF14))
                }
            }

            errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error al cargar noticias: $errorMsg",
                        color = Color.White
                    )
                }
            }

            noticias.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay noticias disponibles por ahora",
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(padding)
                ) {
                    items(noticias) { noticia ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    // Abrir la noticia en el navegador
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(noticia.article_url)
                                    )
                                    context.startActivity(intent)
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Image(
                                    painter = rememberAsyncImagePainter(noticia.thumbnail),
                                    contentDescription = noticia.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    noticia.title,
                                    color = Color(0xFF39FF14),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    noticia.short_description,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = Color(0xFF1E90FF), thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
