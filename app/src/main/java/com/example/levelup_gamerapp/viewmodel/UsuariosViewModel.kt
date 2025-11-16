package com.example.levelup_gamerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.repository.RegistroUsuarioRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel dedicado a la administración de usuarios. Permite observar la lista
 * completa de usuarios, obtener usuarios individuales, así como crear, actualizar
 * y eliminar registros. Está pensado para ser utilizado en las pantallas de
 * administración donde un usuario con permisos pueda gestionar las cuentas.
 */
class UsuariosViewModel(private val repository: RegistroUsuarioRepository) : ViewModel() {

    /**
     * Flujo que emite la lista de usuarios. Se comparte en el scope de este
     * ViewModel para no recrear observadores innecesarios.
     */
    val usuarios: StateFlow<List<RegistroUsuarioEntity>> =
        repository.obtenerUsuarios()
            .map { it.sortedBy { user -> user.id } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    /**
     * Devuelve un flujo reactivo con el usuario cuyo id coincida. Puede emitir null.
     */
    fun obtenerUsuarioPorId(id: Int) = repository.obtenerUsuarioPorId(id)

    /**
     * Inserta un nuevo usuario en la base de datos.
     */
    fun registrarUsuario(usuario: RegistroUsuarioEntity) {
        viewModelScope.launch {
            repository.registrarUsuario(usuario)
        }
    }

    /**
     * Actualiza un usuario existente.
     */
    fun actualizarUsuario(usuario: RegistroUsuarioEntity) {
        viewModelScope.launch {
            repository.actualizarUsuario(usuario)
        }
    }

    /**
     * Elimina un usuario por id.
     */
    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            repository.eliminarUsuario(id)
        }
    }
}

/**
 * Factory para crear instancias de [UsuariosViewModel] proporcionando el
 * repositorio correspondiente.
 */
class UsuariosViewModelFactory(private val repository: RegistroUsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuariosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuariosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}