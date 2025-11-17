package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.remote.CategoriaDTO
import com.example.levelup_gamerapp.repository.RemoteCategoriasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el listado y la creación/actualización/eliminación
 * de categorías a través del backend.
 */
class CategoriasViewModel(
    private val repository: RemoteCategoriasRepository
) : ViewModel() {

    // Flujo interno que contiene la lista de categorías actuales.
    private val _categorias = MutableStateFlow<List<CategoriaDTO>>(emptyList())

    /**
     * Flujo observable para la UI.
     */
    val categorias: StateFlow<List<CategoriaDTO>> = _categorias.asStateFlow()

    /**
     * Recarga la lista de categorías desde el servidor.
     */
    fun cargarCategorias() {
        viewModelScope.launch {
            _categorias.value = repository.obtenerCategorias()
        }
    }

    /**
     * Crea una nueva categoría y recarga la lista.
     */
    fun crearCategoria(categoria: CategoriaDTO) {
        viewModelScope.launch {
            repository.crearCategoria(categoria)
            cargarCategorias()
        }
    }

    /**
     * Actualiza una categoría existente y recarga la lista.
     */
    fun actualizarCategoria(id: Long, categoria: CategoriaDTO) {
        viewModelScope.launch {
            repository.actualizarCategoria(id, categoria)
            cargarCategorias()
        }
    }

    /**
     * Elimina una categoría y recarga la lista.
     */
    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            repository.eliminarCategoria(id)
            cargarCategorias()
        }
    }
}

/**
 * Factory para instanciar [CategoriasViewModel] con su repositorio inyectado.
 */
class CategoriasViewModelFactory(
    private val repository: RemoteCategoriasRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriasViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
