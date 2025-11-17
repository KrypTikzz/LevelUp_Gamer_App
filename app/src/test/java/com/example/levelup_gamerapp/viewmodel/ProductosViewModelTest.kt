package com.example.levelup_gamerapp.viewmodel

import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [ProductosViewModel].
 *
 * Se utiliza un repositorio remoto falso para interceptar las operaciones de
 * carga, creación y eliminación de productos. Se verifica que el StateFlow
 * 'productos' del ViewModel se actualiza correctamente después de cada
 * operación.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductosViewModelTest {

    private lateinit var fakeRepo: FakeRemoteProductosRepository
    private lateinit var viewModel: ProductosViewModel

    @Before
    fun setUp() {
        fakeRepo = FakeRemoteProductosRepository()
        viewModel = ProductosViewModel(fakeRepo)
    }

    @Test
    fun cargarProductos_emiteListaRecibidaDelRepositorio() = runTest(UnconfinedTestDispatcher()) {
        val lista = listOf(
            ProductoDTO(1L, "A", "Desc", 1.0, "url"),
            ProductoDTO(2L, "B", "Desc", 2.0, "url")
        )
        fakeRepo.lista = lista.toMutableList()
        viewModel.cargarProductos()
        advanceUntilIdle()
        assertEquals(2, viewModel.productos.value.size)
        assertEquals("A", viewModel.productos.value[0].nombre)
        assertEquals("B", viewModel.productos.value[1].nombre)
    }

    @Test
    fun agregarProducto_agregaYRecargaLista() = runTest(UnconfinedTestDispatcher()) {
        fakeRepo.lista = mutableListOf()
        val nuevo = ProductoDTO(3L, "Nuevo", "Desc", 5.0, "url")
        viewModel.agregarProducto(nuevo)
        advanceUntilIdle()
        assertEquals(1, viewModel.productos.value.size)
        assertEquals("Nuevo", viewModel.productos.value[0].nombre)
    }

    @Test
    fun eliminarProducto_eliminaYRecargaLista() = runTest(UnconfinedTestDispatcher()) {
        fakeRepo.lista = mutableListOf(
            ProductoDTO(1L, "A", "", 1.0, ""),
            ProductoDTO(2L, "B", "", 2.0, "")
        )
        viewModel.cargarProductos()
        advanceUntilIdle()
        assertEquals(2, viewModel.productos.value.size)
        viewModel.eliminarProducto(1L)
        advanceUntilIdle()
        assertEquals(1, viewModel.productos.value.size)
        assertEquals(2L, viewModel.productos.value[0].id)
    }

    /**
     * Repositorio remoto falso para productos. Almacena una lista mutable
     * interna y simula las operaciones de red del backend.
     */
    private class FakeRemoteProductosRepository : RemoteProductosRepository() {
        var lista: MutableList<ProductoDTO> = mutableListOf()
        override suspend fun obtenerProductos(): List<ProductoDTO> {
            return lista
        }
        override suspend fun crearProducto(producto: ProductoDTO): ProductoDTO {
            lista.add(producto)
            return producto
        }
        override suspend fun eliminarProducto(id: Long) {
            lista.removeIf { it.id == id }
        }
    }
}