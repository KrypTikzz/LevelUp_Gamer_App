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
 * ViewModel que gestiona el listado y la creación de categorías a través del
 * backend. Expone un flujo de categorías y métodos para recargar y crear
 * categorías. Utiliza un [RemoteCategoriasRepository] para realizar las
 * operaciones de red.
 */
class CategoriasViewModel(private val repository: RemoteCategoriasRepository) : ViewModel() {
    // Flujo interno que contiene la lista de categorías actuales.
    private val _categorias = MutableStateFlow<List<CategoriaDTO>>(emptyList())

    /**
     * Flujo observable para la UI. Se emite una lista actualizada cada vez
     * que se recargan las categorías o se crea una nueva.
     */
    val categorias: StateFlow<List<CategoriaDTO>> = _categorias.asStateFlow()

    /**
     * Recarga la lista de categorías desde el servidor. Si ocurre una
     * excepción, se propagará para ser gestionada por la UI.
     */
    fun cargarCategorias() {
        viewModelScope.launch {
            _categorias.value = repository.obtenerCategorias()
        }
    }

    /**
     * Envía una solicitud de creación de categoría al backend y recarga la
     * lista si la operación tiene éxito. Cualquier excepción se propagará
     * hacia la capa de presentación.
     *
     * @param categoria La categoría a crear.
     */
    fun crearCategoria(categoria: CategoriaDTO) {
        viewModelScope.launch {
            repository.crearCategoria(categoria)
            cargarCategorias()
        }
    }
}

/**
 * Factory para instanciar [CategoriasViewModel] con su repositorio inyectado.
 */
class CategoriasViewModelFactory(private val repository: RemoteCategoriasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriasViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}