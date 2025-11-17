package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.local.ProductosDao
import com.example.levelup_gamerapp.local.ProductosEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pruebas para [ProductosRepository] usando un [ProductosDao] falso. Se verifica que
 * las operaciones delegan correctamente en el DAO y que los métodos devuelven
 * los resultados esperados.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProductosRepositoryTest {

    private lateinit var dao: FakeProductosDao
    private lateinit var repository: ProductosRepository

    @Before
    fun setUp() {
        dao = FakeProductosDao()
        repository = ProductosRepository(dao)
    }

    @Test
    fun obtenerProductos_devuelveListaDelDao() = runTest(UnconfinedTestDispatcher()) {
        dao.items.addAll(
            listOf(
                ProductosEntity(1, "A", "", 1.0, 10, 100L),
                ProductosEntity(2, "B", "", 2.0, 5, 200L)
            )
        )
        val result = repository.obtenerProductos()
        assertEquals(2, result.size)
        assertEquals("A", result[0].nombre)
    }

    @Test
    fun insertarProducto_agregaProductoAlDao() = runTest(UnconfinedTestDispatcher()) {
        val nuevo = ProductosEntity(1, "Nuevo", "", 3.0, 2, 300L)
        repository.insertarProducto(nuevo)
        assertEquals(1, dao.items.size)
        assertEquals("Nuevo", dao.items[0].nombre)
    }

    @Test
    fun eliminarProducto_eliminaProductoDelDao() = runTest(UnconfinedTestDispatcher()) {
        val a = ProductosEntity(1, "A", "", 1.0, 1, 100L)
        val b = ProductosEntity(2, "B", "", 2.0, 1, 200L)
        dao.items.addAll(listOf(a, b))
        repository.eliminarProducto(a)
        assertEquals(1, dao.items.size)
        assertEquals(b, dao.items[0])
    }

    @Test
    fun eliminarTodos_vaciaListaDelDao() = runTest(UnconfinedTestDispatcher()) {
        dao.items.add(ProductosEntity(1, "A", "", 1.0, 1, 100L))
        repository.eliminarTodos()
        assertEquals(0, dao.items.size)
    }

    @Test
    fun obtenerProductoPorId_devuelveProductoCorrecto() = runTest(UnconfinedTestDispatcher()) {
        val p = ProductosEntity(5, "P", "", 1.0, 1, 500L)
        dao.items.add(p)
        val res = repository.obtenerProductoPorId(5)
        assertEquals(p, res)
    }

    @Test
    fun obtenerProductoPorRemoteId_devuelveProductoCorrecto() = runTest(UnconfinedTestDispatcher()) {
        val p = ProductosEntity(7, "P", "", 1.0, 1, 700L)
        dao.items.add(p)
        val res = repository.obtenerProductoPorRemoteId(700L)
        assertEquals(p, res)
    }

    @Test
    fun observarProductos_emiteListaActual() = runTest(UnconfinedTestDispatcher()) {
        dao.items.add(ProductosEntity(1, "X", "", 1.0, 1, 100L))
        val first = repository.observarProductos().first()
        assertEquals(1, first.size)
        assertEquals("X", first[0].nombre)
    }

    /**
     * DAO falso para productos que almacena los elementos en memoria.
     */
    private class FakeProductosDao : ProductosDao {
        val items = mutableListOf<ProductosEntity>()
        override suspend fun obtenerTodos(): List<ProductosEntity> = items
        override fun observarTodos(): Flow<List<ProductosEntity>> = flow { emit(items) }
        override suspend fun insertarProducto(producto: ProductosEntity) {
            items.removeIf { it.id == producto.id }
            items.add(producto)
        }
        override suspend fun eliminarProducto(producto: ProductosEntity) {
            items.remove(producto)
        }
        override suspend fun eliminarTodos() {
            items.clear()
        }
        override suspend fun obtenerProductoPorId(id: Int): ProductosEntity? {
            return items.firstOrNull { it.id == id }
        }
        override suspend fun obtenerProductoPorNombre(nombre: String): ProductosEntity? {
            return items.firstOrNull { it.nombre == nombre }
        }
        override suspend fun actualizarProducto(producto: ProductosEntity) {
            eliminarProducto(producto)
            insertarProducto(producto)
        }
        override suspend fun obtenerProductoPorRemoteId(remoteId: Long): ProductosEntity? {
            return items.firstOrNull { it.remoteId == remoteId }
        }
    }
}