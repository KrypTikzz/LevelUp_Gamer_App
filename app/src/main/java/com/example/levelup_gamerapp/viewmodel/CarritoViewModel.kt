package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.local.CarritoEntity
import com.example.levelup_gamerapp.repository.CarritoRepository
import kotlinx.coroutines.launch

class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {

    val carrito = repository.carrito

    fun agregarProductoAlCarrito(nombre: String, precio: Double, imagenUrl: String) {
        viewModelScope.launch {
            val item = CarritoEntity(
                nombreProducto = nombre,
                precio = precio,
                cantidad = 1,
                imagenUrl = imagenUrl
            )
            repository.agregar(item)
        }
    }

    fun eliminarProducto(item: CarritoEntity) {
        viewModelScope.launch {
            repository.eliminar(item)
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciar()
        }
    }
}

