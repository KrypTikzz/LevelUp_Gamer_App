package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.remote.UsuarioDTO
import com.example.levelup_gamerapp.repository.RemoteUsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel dedicado a la gestión de usuarios a través del backend. Sustituye
 * la versión anterior que utilizaba Room para almacenar usuarios localmente.
 * Este ViewModel utiliza [RemoteUsuariosRepository] para listar y crear
 * usuarios en el servidor. Los datos se exponen mediante un [StateFlow] para
 * que la interfaz pueda reaccionar a los cambios.
 */
class UsuariosViewModel(private val repository: RemoteUsuariosRepository) : ViewModel() {
    // Flujo interno de usuarios
    private val _usuarios = MutableStateFlow<List<UsuarioDTO>>(emptyList())
    /** Flujo observable para la UI */
    val usuarios: StateFlow<List<UsuarioDTO>> = _usuarios.asStateFlow()

    /** Recupera la lista de usuarios desde el servidor. */
    fun cargarUsuarios() {
        viewModelScope.launch {
            _usuarios.value = repository.obtenerUsuarios()
        }
    }

    /** Crea un nuevo usuario en el backend y recarga la lista. */
    fun crearUsuario(usuario: UsuarioDTO) {
        viewModelScope.launch {
            repository.crearUsuario(usuario)
            cargarUsuarios()
        }
    }
}

/**
 * Factory para crear instancias de [UsuariosViewModel] inyectando el
 * repositorio remoto. Permite desacoplar la creación del ViewModel de la
 * lógica de la actividad o fragmento.
 */
class UsuariosViewModelFactory(private val repository: RemoteUsuariosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuariosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuariosViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}