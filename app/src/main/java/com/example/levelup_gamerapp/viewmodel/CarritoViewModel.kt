package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.local.CarritoEntity
import com.example.levelup_gamerapp.repository.CarritoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona las operaciones relacionadas con el carrito de compras.
 *
 * Se ha modificado el métoodo agregarProductoAlCarrito para incluir el
 * parámetro idProducto, que corresponde al identificador real del producto.
 */
class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {

    /** Flujo con la lista de ítems del carrito. */
    val carrito = repository.carrito

    /**
     * Agrega un producto al carrito.  Se recibe el identificador del
     * producto ([idProducto]) para poder asociar este ítem con el producto
     * correspondiente en la base de datos remota.
     */
    fun agregarProductoAlCarrito(
        idProducto: Long,
        nombre: String,
        precio: Double,
        imagenUrl: String
    ) {
        viewModelScope.launch {
            val item = CarritoEntity(
                idProducto = idProducto,
                nombreProducto = nombre,
                precio = precio,
                cantidad = 1,
                imagenUrl = imagenUrl
            )
            repository.agregar(item)
        }
    }

    /** Elimina un ítem del carrito. */
    fun eliminarProducto(item: CarritoEntity) {
        viewModelScope.launch {
            repository.eliminar(item)
        }
    }

    /** Vacía completamente el carrito. */
    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciar()
        }
    }
}