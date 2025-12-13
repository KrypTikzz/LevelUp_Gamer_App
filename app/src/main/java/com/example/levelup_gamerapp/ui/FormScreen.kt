package com.example.levelup_gamerapp.ui

// Ya no solicitamos permisos de almacenamiento amplios para acceder a la galería.
import android.graphics.Bitmap
import android.net.Uri
// La clase Build no se utiliza directamente después de eliminar la lógica de permisos
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
// ContextCompat ya no es necesario después de eliminar las comprobaciones de permisos
import com.example.levelup_gamerapp.viewmodel.RegistroUsuarioViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
// import androidx.compose.ui.text.input.KeyboardOptions   // ← ya no se usa

/**
 * Formulario de registro de usuarios. Contiene campos de texto, selección de foto
 * y lógica de validación. Al registrar correctamente un usuario se ejecuta
 * [onSaved], que normalmente navega a la pantalla de login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    vm: RegistroUsuarioViewModel,
    onSaved: () -> Unit
) {
    // Estados locales para los campos de texto
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var foto by remember { mutableStateOf<Bitmap?>(null) }

    val mensaje by vm.mensaje.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // Cámara y galería
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> bitmap?.let { foto = it } }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val thumb = context.contentResolver.loadThumbnail(it, Size(200, 200), null)
            foto = thumb
        }
    }
    /*
     * En la implementación anterior se intentaba solicitar permisos de almacenamiento
     * amplios antes de lanzar el selector de galería del sistema. Sin embargo,
     * el contrato `GetContent` no requiere los permisos `READ_EXTERNAL_STORAGE`
     * ni `READ_MEDIA_IMAGES`. Cuando el usuario selecciona un elemento a través
     * de este selector, el sistema concede acceso temporal al URI seleccionado.
     * Solicitar un permiso de almacenamiento sin declararlo en el manifiesto
     * provoca que la solicitud falle silenciosamente y que la galería nunca se abra.
     *
     * Para solucionar el problema de que el botón de galería no hacía nada,
     * eliminamos la lógica innecesaria de permisos y lanzamos directamente el
     * selector mediante `GetContent`. Este enfoque funciona en todos los niveles
     * de API soportados y respeta el modelo de almacenamiento restringido de
     * Android. Si necesitas un control más fino (por ejemplo, usar el nuevo
     * photo picker en Android 13+), puedes considerar el contrato
     * `ActivityResultContracts.PickVisualMedia`.
     */

    // Ejecuta onSaved solo cuando el mensaje reporta éxito
    LaunchedEffect(mensaje) {
        if (mensaje.startsWith("Registro exitoso")) {
            onSaved()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            Text(
                "Registro de Usuario",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Contenedor de foto
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                        shape = CircleShape
                    )
                    .background(Color.DarkGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (foto != null) {
                    Image(
                        bitmap = foto!!.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Sin foto", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botones para tomar o seleccionar foto
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { cameraLauncher.launch(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Tomar foto", color = Color.Black)
                }
                Button(
                    // Lanza el selector de galería directamente sin solicitar permisos extra.
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Subir desde galería", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campos de texto
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                // Ocultar la contraseña con asteriscos ~ teclado de contraseña no es obligatorio
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val edadInt = edad.toIntOrNull() ?: 0
                    vm.registrar(nombre, apellido, correo, contrasena, edadInt, foto)
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrarse")
            }

            if (mensaje.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mensaje,
                    color = when {
                        mensaje.startsWith("Registro exitoso") -> MaterialTheme.colorScheme.secondary
                        mensaje.contains("descuento", ignoreCase = true) -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}
