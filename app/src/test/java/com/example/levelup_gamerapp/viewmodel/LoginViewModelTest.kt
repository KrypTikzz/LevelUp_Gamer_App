package com.example.levelup_gamerapp.viewmodel

import com.example.levelup_gamerapp.local.RegistroUsuarioDAO
import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.repository.LoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [LoginViewModel].
 *
 * Se utiliza un RegistroUsuarioDAO falso para simular la existencia de usuarios
 * en la base de datos. Se comprueban distintos caminos de la lógica de inicio
 * de sesión: campos vacíos, usuario inexistente, contraseña incorrecta y
 * credenciales válidas (incluyendo el caso de administrador).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var fakeDao: FakeRegistroUsuarioDAO
    private lateinit var repository: LoginRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        fakeDao = FakeRegistroUsuarioDAO()
        repository = LoginRepository(fakeDao)
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun iniciarSesion_camposEnBlanco_muestraMensajeCompletaCampos() = runTest(UnconfinedTestDispatcher()) {
        viewModel.iniciarSesion("", "")
        advanceUntilIdle()
        assertEquals("Completa todos los campos", viewModel.mensaje.value)
        assertEquals(false, viewModel.loginExitoso.value)
        assertEquals(false, viewModel.esAdmin.value)
    }

    @Test
    fun iniciarSesion_usuarioNoEncontrado_muestraMensajeUsuarioNoEncontrado() = runTest(UnconfinedTestDispatcher()) {
        viewModel.iniciarSesion("no@existe.com", "1234")
        advanceUntilIdle()
        assertEquals("Usuario no encontrado", viewModel.mensaje.value)
        assertEquals(false, viewModel.loginExitoso.value)
        assertEquals(false, viewModel.esAdmin.value)
    }

    @Test
    fun iniciarSesion_contrasenaIncorrecta_muestraMensajeContrasenaIncorrecta() = runTest(UnconfinedTestDispatcher()) {
        // Registramos un usuario en el DAO con una contraseña diferente
        fakeDao.usuarios["test@correo.com"] = RegistroUsuarioEntity(
            id = 1,
            nombre = "Test",
            apellido = "User",
            correo = "test@correo.com",
            contrasena = "correcta",
            edad = 20,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        viewModel.iniciarSesion("test@correo.com", "incorrecta")
        advanceUntilIdle()
        assertEquals("Contraseña incorrecta", viewModel.mensaje.value)
        assertEquals(false, viewModel.loginExitoso.value)
        assertEquals(false, viewModel.esAdmin.value)
    }

    @Test
    fun iniciarSesion_loginExitosoUsuarioNormal_actualizaEstados() = runTest(UnconfinedTestDispatcher()) {
        // Usuario normal con correo diferente al admin predeterminado
        fakeDao.usuarios["usuario@correo.com"] = RegistroUsuarioEntity(
            id = 2,
            nombre = "Normal",
            apellido = "User",
            correo = "usuario@correo.com",
            contrasena = "pass",
            edad = 25,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        viewModel.iniciarSesion("usuario@correo.com", "pass")
        advanceUntilIdle()
        assertEquals("Inicio de sesión exitoso 🎮", viewModel.mensaje.value)
        assertEquals(true, viewModel.loginExitoso.value)
        // Debe ser falso porque no usa el correo de admin
        assertEquals(false, viewModel.esAdmin.value)
    }

    @Test
    fun iniciarSesion_loginExitosoAdmin_determinaEsAdmin() = runTest(UnconfinedTestDispatcher()) {
        // Usuario con correo administrador
        fakeDao.usuarios["admin@levelupgamer.cl"] = RegistroUsuarioEntity(
            id = 3,
            nombre = "Admin",
            apellido = "User",
            correo = "admin@levelupgamer.cl",
            contrasena = "adminpass",
            edad = 30,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        viewModel.iniciarSesion("admin@levelupgamer.cl", "adminpass")
        advanceUntilIdle()
        assertEquals("Inicio de sesión exitoso 🎮", viewModel.mensaje.value)
        assertEquals(true, viewModel.loginExitoso.value)
        assertEquals(true, viewModel.esAdmin.value)
    }

    @Test
    fun resetEstado_reiniciaLoginExitosoYMensaje() = runTest(UnconfinedTestDispatcher()) {
        // Estado inicial exitoso
        fakeDao.usuarios["user@correo.com"] = RegistroUsuarioEntity(
            id = 4,
            nombre = "U",
            apellido = "S",
            correo = "user@correo.com",
            contrasena = "pass",
            edad = 22,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        viewModel.iniciarSesion("user@correo.com", "pass")
        advanceUntilIdle()
        // Ahora reset
        viewModel.resetEstado()
        assertEquals(false, viewModel.loginExitoso.value)
        assertEquals("", viewModel.mensaje.value)
    }

    @Test
    fun limpiarMensaje_estableceMensajeVacio_sinAfectarLoginExitoso() = runTest(UnconfinedTestDispatcher()) {
        fakeDao.usuarios["user2@correo.com"] = RegistroUsuarioEntity(
            id = 5,
            nombre = "U2",
            apellido = "S2",
            correo = "user2@correo.com",
            contrasena = "pass",
            edad = 22,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        viewModel.iniciarSesion("user2@correo.com", "pass")
        advanceUntilIdle()
        viewModel.limpiarMensaje()
        assertEquals("", viewModel.mensaje.value)
        // El estado de éxito debería permanecer como estaba
        assertEquals(true, viewModel.loginExitoso.value)
    }

    /**
     * Fake de [RegistroUsuarioDAO] que almacena usuarios en memoria para las pruebas.
     * Sólo se implementan los métodos usados por [LoginRepository].
     */
    private class FakeRegistroUsuarioDAO : RegistroUsuarioDAO {
        val usuarios: MutableMap<String, RegistroUsuarioEntity> = mutableMapOf()
        override suspend fun insertarUsuario(usuario: RegistroUsuarioEntity) {
            usuarios[usuario.correo] = usuario
        }
        override suspend fun buscarPorCorreo(correo: String): RegistroUsuarioEntity? {
            return usuarios[correo]
        }
        override suspend fun eliminarPorCorreo(correo: String) {
            usuarios.remove(correo)
        }
        override fun obtenerTodosUsuarios(): kotlinx.coroutines.flow.Flow<List<RegistroUsuarioEntity>> {
            throw NotImplementedError("No se utiliza en estas pruebas")
        }
        override fun obtenerUsuarioPorId(id: Int): kotlinx.coroutines.flow.Flow<RegistroUsuarioEntity?> {
            throw NotImplementedError("No se utiliza en estas pruebas")
        }
        override suspend fun actualizarUsuario(usuario: RegistroUsuarioEntity) {
            usuarios[usuario.correo] = usuario
        }
        override suspend fun eliminarPorId(id: Int) {
            // No se necesita para estas pruebas
        }
    }
}