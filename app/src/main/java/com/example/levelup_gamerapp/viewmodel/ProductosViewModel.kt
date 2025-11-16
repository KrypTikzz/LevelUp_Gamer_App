package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.remote.ProductoDTO
import com.example.levelup_gamerapp.repository.RemoteProductosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel dedicado a la gestión de productos a través del backend. A diferencia
 * de la versión que interactuaba con Room, esta implementación utiliza un
 * repositorio remoto para obtener, crear y eliminar productos. Los datos se
 * exponen mediante un [StateFlow] para que la UI pueda reaccionar a los
 * cambios cuando se realizan operaciones de red.
 */
class ProductosViewModel(private val repository: RemoteProductosRepository) : ViewModel() {

    // Flujo interno que contiene la lista de productos recibidos del backend
    private val _productos = MutableStateFlow<List<ProductoDTO>>(emptyList())

    /** Flujo observable para la UI. */
    val productos: StateFlow<List<ProductoDTO>> = _productos.asStateFlow()

    /**
     * Recupera la lista de productos desde el servidor. Si la petición falla,
     * la excepción se propagará hacia arriba y debería ser manejada por la UI.
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = repository.obtenerProductos()
        }
    }

    /**
     * Envía una solicitud para crear un nuevo producto en el backend y recarga
     * la lista una vez que la creación finaliza con éxito.
     */
    fun agregarProducto(producto: ProductoDTO) {
        viewModelScope.launch {
            repository.crearProducto(producto)
            cargarProductos()
        }
    }

    /**
     * Elimina un producto remoto por su identificador y recarga la lista
     * posteriormente.
     */
    fun eliminarProducto(id: Long) {
        viewModelScope.launch {
            repository.eliminarProducto(id)
            cargarProductos()
        }
    }
}