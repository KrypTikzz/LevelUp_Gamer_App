package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelup_gamerapp.repository.CarritoRepository

class CarritoViewModelFactory(private val repo: CarritoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel(repo) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}

