package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelup_gamerapp.repository.RemoteProductosRepository

/**
 * Factory para crear instancias de [ProductosViewModel] inyectando el
 * repositorio remoto correspondiente. Este patrón permite que el ViewModel
 * reciba dependencias sin necesidad de utilizar frameworks de inyección más
 * complejos.
 */
class ProductosViewModelFactory(private val repository: RemoteProductosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductosViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}