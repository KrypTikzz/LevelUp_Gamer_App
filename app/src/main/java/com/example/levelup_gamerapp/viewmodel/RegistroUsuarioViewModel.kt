package com.example.levelup_gamerapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.repository.RegistroUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class RegistroUsuarioViewModel(private val repository: RegistroUsuarioRepository) : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun registrar(
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        edad: Int,
        foto: Bitmap? = null // 🆕 nuevo parámetro opcional
    ) {
        viewModelScope.launch {

            // ---- VALIDACIONES ----
            if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                _mensaje.value = "Completa todos los campos"
                return@launch
            }

            val soloLetras = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")
            if (!soloLetras.matches(nombre)) {
                _mensaje.value = "El nombre solo puede contener letras"
                return@launch
            }
            if (!soloLetras.matches(apellido)) {
                _mensaje.value = "El apellido solo puede contener letras"
                return@launch
            }

            val correoRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!correoRegex.matches(correo)) {
                _mensaje.value = "Correo electrónico inválido"
                return@launch
            }

            val contrasenaSegura = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
            if (!contrasenaSegura.matches(contrasena)) {
                _mensaje.value =
                    "La contraseña debe tener al menos 6 caracteres e incluir letras y números"
                return@launch
            }

            if (edad < 18) {
                _mensaje.value = "Debes ser mayor de 18 años"
                return@launch
            }

            val existente = repository.verificarCorreo(correo)
            if (existente != null) {
                _mensaje.value = "El correo ya está registrado"
                return@launch
            }

            val dominiosDescuento = listOf("@duocuc.cl")
            val descuento = if (dominiosDescuento.any { correo.endsWith(it, true) }) 20 else 0

            // 🧠 Convierte la foto en bytes (si existe)
            val fotoBytes = foto?.let {
                val stream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }

            val usuario = RegistroUsuarioEntity(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                correo = correo.trim(),
                contrasena = contrasena,
                edad = edad,
                descuentoAplicado = descuento,
                fotoPerfil = fotoBytes // 🆕 guardamos los bytes
            )

            repository.registrarUsuario(usuario)

            _mensaje.value = if (descuento > 0)
                "Registro exitoso 🎉 Se aplicó un descuento del 20%"
            else
                "Registro exitoso ✅"
        }
    }
}
