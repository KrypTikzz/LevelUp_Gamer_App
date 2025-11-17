package com.example.levelup_gamerapp.viewmodel

import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.repository.RegistroUsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [RegistroUsuarioViewModel].
 *
 * Se utiliza un repositorio falso para interceptar las llamadas de registro y
 * para simular la verificación de correo existente. Las pruebas cubren todas
 * las ramas de validación: campos vacíos, nombre y apellido inválidos,
 * correo y contraseña con formato incorrecto, edad mínima, correo existente
 * y los casos exitosos con o sin descuento.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegistroUsuarioViewModelTest {

    private lateinit var fakeRepo: FakeRegistroUsuarioRepository
    private lateinit var viewModel: RegistroUsuarioViewModel

    @Before
    fun setUp() {
        fakeRepo = FakeRegistroUsuarioRepository()
        viewModel = RegistroUsuarioViewModel(fakeRepo)
    }

    @Test
    fun registrar_camposVacios_muestraMensajeCompletaCampos() = runTest(UnconfinedTestDispatcher()) {
        viewModel.registrar("", "", "", "", 20, null)
        advanceUntilIdle()
        assertEquals("Completa todos los campos", viewModel.mensaje.value)
    }

    @Test
    fun registrar_nombreInvalido_muestraErrorNombre() = runTest(UnconfinedTestDispatcher()) {
        viewModel.registrar("John123", "Doe", "john@example.com", "abc123", 20, null)
        advanceUntilIdle()
        assertEquals("El nombre solo puede contener letras", viewModel.mensaje.value)
    }

    @Test
    fun registrar_apellidoInvalido_muestraErrorApellido() = runTest(UnconfinedTestDispatcher()) {
        viewModel.registrar("John", "Doe123", "john@example.com", "abc123", 20, null)
        advanceUntilIdle()
        assertEquals("El apellido solo puede contener letras", viewModel.mensaje.value)
    }

    @Test
    fun registrar_correoInvalido_muestraErrorCorreo() = runTest(UnconfinedTestDispatcher()) {
        viewModel.registrar("John", "Doe", "correoInvalido", "abc123", 20, null)
        advanceUntilIdle()
        assertEquals("Correo electrónico inválido", viewModel.mensaje.value)
    }

    @Test
    fun registrar_contrasenaInsegura_muestraErrorContrasena() = runTest(UnconfinedTestDispatcher()) {
        viewModel.registrar("John", "Doe", "john@example.com", "abc", 20, null)
        advanceUntilIdle()
        assertEquals(
            "La contraseña debe tener al menos 6 caracteres e incluir letras y números",
            viewModel.mensaje.value
        )
    }

    @Test
    fun registrar_edadMenor18_muestraErrorEdad() = runTest(UnconfinedTestDispatcher()) {
        viewModel.registrar("John", "Doe", "john@example.com", "abc123", 17, null)
        advanceUntilIdle()
        assertEquals("Debes ser mayor de 18 años", viewModel.mensaje.value)
    }

    @Test
    fun registrar_correoExistente_muestraErrorCorreoExistente() = runTest(UnconfinedTestDispatcher()) {
        // Simulamos que el correo ya existe
        fakeRepo.existingEmails.add("existente@correo.com")
        viewModel.registrar("Jane", "Doe", "existente@correo.com", "abc123", 25, null)
        advanceUntilIdle()
        assertEquals("El correo ya está registrado", viewModel.mensaje.value)
    }

    @Test
    fun registrar_registroExitosoSinDescuento_enviaEntidadCorrecta() = runTest(UnconfinedTestDispatcher()) {
        val correo = "usuario@correo.com"
        viewModel.registrar("Ana", "Perez", correo, "abc123", 30, null)
        advanceUntilIdle()
        // Verificamos que el repositorio haya recibido una entidad y que no haya descuento
        val enviado = fakeRepo.ultimoRegistrado
        requireNotNull(enviado)
        assertEquals(correo, enviado.correo)
        assertEquals(0, enviado.descuentoAplicado)
        assertEquals("Registro exitoso ✅", viewModel.mensaje.value)
    }

    @Test
    fun registrar_registroExitosoConDescuento_aplica20Porciento() = runTest(UnconfinedTestDispatcher()) {
        val correo = "alumno@duocuc.cl"
        viewModel.registrar("Luis", "Lopez", correo, "abc123", 25, null)
        advanceUntilIdle()
        val enviado = fakeRepo.ultimoRegistrado
        requireNotNull(enviado)
        assertEquals(20, enviado.descuentoAplicado)
        assertEquals(
            "Registro exitoso 🎉 Se aplicó un descuento del 20%",
            viewModel.mensaje.value
        )
    }

    /**
     * Repositorio falso para interceptar llamadas del ViewModel.
     * Permite configurar correos existentes y capturar la última entidad
     * registrada para su inspección.
     */
    private class FakeRegistroUsuarioRepository : RegistroUsuarioRepository() {
        // lista de correos existentes simulados
        val existingEmails: MutableSet<String> = mutableSetOf()
        // última entidad registrada
        var ultimoRegistrado: RegistroUsuarioEntity? = null

        override suspend fun registrarUsuario(usuario: RegistroUsuarioEntity) {
            ultimoRegistrado = usuario
        }

        override suspend fun verificarCorreo(correo: String): RegistroUsuarioEntity? {
            return if (existingEmails.contains(correo)) {
                // devolvemos una entidad para indicar que ya existe
                RegistroUsuarioEntity(
                    id = 1,
                    nombre = "Existente",
                    apellido = "Usuario",
                    correo = correo,
                    contrasena = "",
                    edad = 30,
                    descuentoAplicado = 0,
                    fotoPerfil = null
                )
            } else null
        }

        // No necesitamos implementar el resto de métodos para estas pruebas
    }
}