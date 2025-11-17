package com.example.levelup_gamerapp.viewmodel

import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.repository.RemoteUsuariosRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [UsuariosViewModel]. Se emplea un repositorio
 * remoto falso para simular las operaciones de obtener, crear y eliminar
 * usuarios a través de la red. Se verifica que el StateFlow 'usuarios'
 * se actualice adecuadamente después de cada operación.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UsuariosViewModelTest {
    private lateinit var fakeRepo: FakeRemoteUsuariosRepository
    private lateinit var viewModel: UsuariosViewModel

    @Before
    fun setUp() {
        fakeRepo = FakeRemoteUsuariosRepository()
        viewModel = UsuariosViewModel(fakeRepo)
    }

    @Test
    fun cargarUsuarios_emiteListaCorrecta() = runTest(UnconfinedTestDispatcher()) {
        fakeRepo.lista = mutableListOf(
            UsuarioDTO(1L, "A", "B", "a@b.com", "", 18, false),
            UsuarioDTO(2L, "C", "D", "c@d.com", "", 19, false)
        )
        viewModel.cargarUsuarios()
        advanceUntilIdle()
        assertEquals(2, viewModel.usuarios.value.size)
        assertEquals("A", viewModel.usuarios.value[0].nombre)
        assertEquals("C", viewModel.usuarios.value[1].nombre)
    }

    @Test
    fun crearUsuario_agregaYRecarga() = runTest(UnconfinedTestDispatcher()) {
        fakeRepo.lista = mutableListOf()
        val nuevo = UsuarioDTO(null, "New", "User", "new@user.com", "", 20, false)
        viewModel.crearUsuario(nuevo)
        advanceUntilIdle()
        assertEquals(1, viewModel.usuarios.value.size)
        assertEquals("New", viewModel.usuarios.value[0].nombre)
    }

    @Test
    fun eliminarUsuario_quitaYRecarga() = runTest(UnconfinedTestDispatcher()) {
        fakeRepo.lista = mutableListOf(
            UsuarioDTO(1L, "X", "Y", "x@y.com", "", 25, false),
            UsuarioDTO(2L, "Z", "W", "z@w.com", "", 30, false)
        )
        viewModel.cargarUsuarios()
        advanceUntilIdle()
        viewModel.eliminarUsuario(1L)
        advanceUntilIdle()
        assertEquals(1, viewModel.usuarios.value.size)
        assertEquals(2L, viewModel.usuarios.value[0].id)
    }

    /**
     * Fake de [RemoteUsuariosRepository] para las pruebas del ViewModel.
     * Mantiene una lista mutable de usuarios y realiza operaciones sobre ella.
     */
    private class FakeRemoteUsuariosRepository : RemoteUsuariosRepository() {
        var lista: MutableList<UsuarioDTO> = mutableListOf()
        override suspend fun obtenerUsuarios(): List<UsuarioDTO> {
            return lista
        }
        override suspend fun crearUsuario(usuario: UsuarioDTO): UsuarioDTO {
            // Asignamos un id incremental si es nulo
            val id = usuario.id ?: (lista.maxOfOrNull { it.id ?: 0L } ?: 0L) + 1
            val nuevo = usuario.copy(id = id)
            lista.add(nuevo)
            return nuevo
        }
        override suspend fun eliminarUsuario(id: Long) {
            lista.removeIf { (it.id ?: -1L) == id }
        }
    }
}