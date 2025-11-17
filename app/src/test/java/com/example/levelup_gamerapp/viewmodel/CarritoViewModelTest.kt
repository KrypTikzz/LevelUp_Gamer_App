package com.example.levelup_gamerapp.viewmodel

import com.example.levelup_gamerapp.local.CarritoDao
import com.example.levelup_gamerapp.local.CarritoEntity
import com.example.levelup_gamerapp.repository.CarritoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [CarritoViewModel].
 *
 * Se utiliza un CarritoDao falso para verificar que las operaciones del ViewModel
 * delegan correctamente en el repositorio subyacente sin necesidad de Room.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest {

    private lateinit var fakeDao: FakeCarritoDao
    private lateinit var repository: CarritoRepository
    private lateinit var viewModel: CarritoViewModel

    @Before
    fun setUp() {
        fakeDao = FakeCarritoDao()
        repository = CarritoRepository(fakeDao)
        viewModel = CarritoViewModel(repository)
    }

    @Test
    fun agregarProductoAlCarrito_agregaItemConCantidadUno() = runTest(UnconfinedTestDispatcher()) {
        viewModel.agregarProductoAlCarrito(1L, "Test", 9.99, "url")
        // Avanzamos hasta que todas las corrutinas se completen
        advanceUntilIdle()
        assertEquals(1, fakeDao.items.size)
        val item = fakeDao.items.first()
        assertEquals(1L, item.idProducto)
        assertEquals("Test", item.nombreProducto)
        assertEquals(9.99, item.precio, 0.001)
        assertEquals(1, item.cantidad)
    }

    @Test
    fun eliminarProducto_quitaItemDelCarrito() = runTest(UnconfinedTestDispatcher()) {
        // Insertamos dos elementos y eliminamos uno
        viewModel.agregarProductoAlCarrito(1L, "A", 1.0, "")
        viewModel.agregarProductoAlCarrito(2L, "B", 2.0, "")
        advanceUntilIdle()
        val primero = fakeDao.items.first()
        viewModel.eliminarProducto(primero)
        advanceUntilIdle()
        assertEquals(1, fakeDao.items.size)
        assertEquals(2L, fakeDao.items.first().idProducto)
    }

    @Test
    fun vaciarCarrito_eliminaTodosLosElementos() = runTest(UnconfinedTestDispatcher()) {
        // Insertamos elementos y luego vaciamos
        viewModel.agregarProductoAlCarrito(1L, "A", 1.0, "")
        viewModel.agregarProductoAlCarrito(2L, "B", 2.0, "")
        advanceUntilIdle()
        assertEquals(2, fakeDao.items.size)
        viewModel.vaciarCarrito()
        advanceUntilIdle()
        assertEquals(0, fakeDao.items.size)
    }

    /**
     * Implementación de [CarritoDao] que mantiene los elementos en memoria para pruebas.
     */
    private class FakeCarritoDao : CarritoDao {
        val items = mutableListOf<CarritoEntity>()
        override fun obtenerCarrito(): Flow<List<CarritoEntity>> = flow {
            emit(items)
        }
        override suspend fun agregarAlCarrito(item: CarritoEntity) {
            items.add(item)
        }
        override suspend fun eliminarDelCarrito(item: CarritoEntity) {
            items.remove(item)
        }
        override suspend fun vaciarCarrito() {
            items.clear()
        }
    }
}