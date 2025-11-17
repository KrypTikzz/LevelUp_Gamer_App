package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.remote.UsuarioDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [RegistroUsuarioRepository]. Se inyecta un repositorio
 * remoto falso para interceptar las operaciones y así comprobar que los
 * parámetros enviados y los valores devueltos cumplan con las reglas de
 * negocio implementadas en el repositorio.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegistroUsuarioRepositoryTest {

    private lateinit var remote: FakeRemoteUsuariosRepository
    private lateinit var repository: RegistroUsuarioRepository

    @Before
    fun setUp() {
        remote = FakeRemoteUsuariosRepository()
        repository = RegistroUsuarioRepository(remote)
    }

    @Test
    fun registrarUsuario_conCorreoAdmin_asignaAdminEnDto() = runTest(UnconfinedTestDispatcher()) {
        val usuario = RegistroUsuarioEntity(
            id = 0,
            nombre = "Admin",
            apellido = "User",
            correo = "admin@levelup.cl",
            contrasena = "pass123",
            edad = 25,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        repository.registrarUsuario(usuario)
        val enviado = remote.crearUsuarioLlamado
        assertNotNull(enviado)
        // Debe tener admin = true cuando el correo coincide con la regla
        assertEquals(true, enviado!!.admin)
    }

    @Test
    fun registrarUsuario_conCorreoNormal_noAsignaAdmin() = runTest(UnconfinedTestDispatcher()) {
        val usuario = RegistroUsuarioEntity(
            id = 0,
            nombre = "User",
            apellido = "Normal",
            correo = "user@correo.com",
            contrasena = "pass123",
            edad = 25,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        repository.registrarUsuario(usuario)
        val enviado = remote.crearUsuarioLlamado
        assertNotNull(enviado)
        assertEquals(false, enviado!!.admin)
    }

    @Test
    fun verificarCorreo_devuelveEntityCuandoExiste() = runTest(UnconfinedTestDispatcher()) {
        // Preparamos un usuario en el remoto
        val dto = UsuarioDTO(
            id = 10L,
            nombre = "Juan",
            apellido = "Perez",
            correo = "juan@correo.com",
            contrasena = "12345",
            edad = 30,
            admin = false
        )
        remote.buscarPorCorreoResultado = dto
        val entity = repository.verificarCorreo("juan@correo.com")
        assertNotNull(entity)
        assertEquals(10, entity!!.id)
        assertEquals(dto.nombre, entity.nombre)
        assertEquals(dto.apellido, entity.apellido)
        assertEquals(dto.correo, entity.correo)
        // descuentoAplicado y fotoPerfil deberían ser inicializados en 0 y null
        assertEquals(0, entity.descuentoAplicado)
        assertEquals(null, entity.fotoPerfil)
    }

    @Test
    fun verificarCorreo_devuelveNullCuandoNoExiste() = runTest(UnconfinedTestDispatcher()) {
        remote.buscarPorCorreoResultado = null
        val entity = repository.verificarCorreo("desconocido@correo.com")
        assertEquals(null, entity)
    }

    @Test
    fun eliminarPorCorreo_eliminaUsuarioCuandoExiste() = runTest(UnconfinedTestDispatcher()) {
        // Simulamos que existe el usuario con id 5
        remote.buscarPorCorreoResultado = UsuarioDTO(
            id = 5L,
            nombre = "X",
            apellido = "Y",
            correo = "x@y.com",
            contrasena = "",
            edad = 20,
            admin = false
        )
        repository.eliminarPorCorreo("x@y.com")
        assertEquals(5L, remote.eliminarUsuarioLlamado)
    }

    @Test
    fun obtenerUsuarios_emiteListaMapeada() = runTest(UnconfinedTestDispatcher()) {
        remote.obtenerUsuariosResultado = listOf(
            UsuarioDTO(
                id = 1L,
                nombre = "A",
                apellido = "B",
                correo = "a@b.com",
                contrasena = "",
                edad = 18,
                admin = false
            ),
            UsuarioDTO(
                id = 2L,
                nombre = "C",
                apellido = "D",
                correo = "c@d.com",
                contrasena = "",
                edad = 19,
                admin = false
            )
        )
        val lista = repository.obtenerUsuarios().first()
        assertEquals(2, lista.size)
        assertEquals("A", lista[0].nombre)
        assertEquals("C", lista[1].nombre)
    }

    @Test
    fun actualizarUsuario_enviaDtoCorrecto() = runTest(UnconfinedTestDispatcher()) {
        val entity = RegistroUsuarioEntity(
            id = 9,
            nombre = "Z",
            apellido = "W",
            correo = "z@w.com",
            contrasena = "pass",
            edad = 50,
            descuentoAplicado = 0,
            fotoPerfil = null
        )
        repository.actualizarUsuario(entity)
        // Debe llamar a actualizarUsuario con el id y DTO
        assertEquals(9L, remote.actualizarUsuarioLlamadoId)
        val enviado = remote.actualizarUsuarioLlamadoDto
        assertNotNull(enviado)
        assertEquals(entity.nombre, enviado!!.nombre)
        assertEquals(entity.apellido, enviado.apellido)
        assertEquals(entity.correo, enviado.correo)
        assertEquals(entity.contrasena, enviado.contrasena)
        assertEquals(entity.edad, enviado.edad)
    }

    /**
     * Fake de [RemoteUsuariosRepository] que permite configurar los valores de
     * retorno y capturar los parámetros con los que se invoca cada método.
     */
    private class FakeRemoteUsuariosRepository : RemoteUsuariosRepository() {
        // Resultados configurables
        var buscarPorCorreoResultado: UsuarioDTO? = null
        var obtenerUsuariosResultado: List<UsuarioDTO> = emptyList()
        // Parámetros capturados
        var crearUsuarioLlamado: UsuarioDTO? = null
        var eliminarUsuarioLlamado: Long? = null
        var actualizarUsuarioLlamadoId: Long? = null
        var actualizarUsuarioLlamadoDto: UsuarioDTO? = null

        override suspend fun crearUsuario(usuario: UsuarioDTO): UsuarioDTO {
            crearUsuarioLlamado = usuario
            return usuario
        }

        override suspend fun buscarPorCorreo(correo: String): UsuarioDTO? {
            return buscarPorCorreoResultado
        }

        override suspend fun eliminarUsuario(id: Long) {
            eliminarUsuarioLlamado = id
        }

        override suspend fun obtenerUsuarios(): List<UsuarioDTO> {
            return obtenerUsuariosResultado
        }

        override suspend fun obtenerUsuario(id: Long): UsuarioDTO {
            // devolvemos el primer usuario encontrado con ese id o un dummy si no existe
            return obtenerUsuariosResultado.firstOrNull { it.id == id }
                ?: UsuarioDTO(
                    id = id,
                    nombre = "",
                    apellido = "",
                    correo = "",
                    contrasena = "",
                    edad = 0,
                    admin = false
                )
        }

        override suspend fun actualizarUsuario(id: Long, usuario: UsuarioDTO): UsuarioDTO {
            actualizarUsuarioLlamadoId = id
            actualizarUsuarioLlamadoDto = usuario
            return usuario
        }
    }
}