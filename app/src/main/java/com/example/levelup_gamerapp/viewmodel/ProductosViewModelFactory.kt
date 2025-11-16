package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelup_gamerapp.repository.ProductosRepository

class ProductosViewModelFactory(private val repository: ProductosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductosViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
