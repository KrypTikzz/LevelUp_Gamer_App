package com.example.levelup_gamerapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContacto() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf(false) }
    var correoError by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf(false) }

    var presionado by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(if (presionado) 0.9f else 1f, label = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Contacto", color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Tienes dudas o sugerencias?",
                color = Color(0xFF1E90FF),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Colores compatibles con Compose Material 3 actual
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                cursorColor = Color(0xFF39FF14),
                focusedBorderColor = Color(0xFF39FF14),
                unfocusedBorderColor = Color(0xFF1E90FF),
                errorBorderColor = Color.Red,
                focusedLabelColor = Color(0xFF39FF14),
                unfocusedLabelColor = Color(0xFF1E90FF),
                errorLabelColor = Color.Red
            )

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = it.isBlank()
                },
                label = { Text("Nombre") },
                isError = nombreError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            // Campo Correo
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    correoError = !correo.contains("@")
                },
                label = { Text("Correo electrónico") },
                isError = correoError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            // Campo Asunto
            OutlinedTextField(
                value = asunto,
                onValueChange = { asunto = it },
                label = { Text("Asunto (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            // Campo Mensaje
            OutlinedTextField(
                value = mensaje,
                onValueChange = {
                    mensaje = it
                    mensajeError = it.isBlank()
                },
                label = { Text("Mensaje") },
                isError = mensajeError,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón
            Button(
                onClick = {
                    presionado = true
                    nombreError = nombre.isBlank()
                    correoError = !correo.contains("@")
                    mensajeError = mensaje.isBlank()

                    if (!nombreError && !correoError && !mensajeError) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Mensaje enviado con éxito 🎮")
                        }
                        nombre = ""
                        correo = ""
                        asunto = ""
                        mensaje = ""
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor completa los campos obligatorios ⚠️")
                        }
                    }

                    scope.launch {
                        kotlinx.coroutines.delay(150)
                        presionado = false
                    }
                },
                modifier = Modifier
                    .scale(escala)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39FF14))
            ) {
                Text("Enviar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
