package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelup_gamerapp.repository.RegistroUsuarioRepository


class RegistroUsuarioViewModelFactory(private val repository: RegistroUsuarioRepository) :
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroUsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroUsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Viewmodel desconocido")
    }
}