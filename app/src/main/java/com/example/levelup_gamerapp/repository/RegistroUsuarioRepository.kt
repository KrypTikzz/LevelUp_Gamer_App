package com.example.levelup_gamerapp.repository

import com.example.levelup_gamerapp.local.RegistroUsuarioEntity
import com.example.levelup_gamerapp.remote.ApiClient
import com.example.levelup_gamerapp.remote.UsuarioDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import com.example.levelup_gamerapp.remote.dto.RegisterRequestDTO
import com.example.levelup_gamerapp.core.UserSession


/**
 * Repositorio de registro que ahora usa el backend a través de
 * [RemoteUsuariosRepository], pero mantiene las mismas firmas públicas
 * que usaba el ViewModel cuando trabajaba con Room.
 */
open class RegistroUsuarioRepository(
    private val remote: RemoteUsuariosRepository = RemoteUsuariosRepository()
) {

    // ---------- Mapeos entre DTO remoto y Entity local ----------

    private fun dtoToEntity(dto: UsuarioDTO): RegistroUsuarioEntity =
        RegistroUsuarioEntity(
            id = dto.id?.toInt() ?: 0,
            nombre = dto.nombre,
            apellido = dto.apellido,
            correo = dto.correo,
            contrasena = dto.contrasena,
            edad = dto.edad,
            // Por ahora el backend no maneja estos campos
            descuentoAplicado = 0,
            fotoPerfil = null
        )

    // ---------- Métodos que usa el ViewModel de registro ----------

    /**
     * Registra un usuario en el backend.
     * La Entity se usa como contenedor de datos en la app.
     */
    open suspend fun registrarUsuario(usuario: RegistroUsuarioEntity) {
        val anioNacimiento = LocalDate.now().year - usuario.edad
        val fechaNacimientoStr = "$anioNacimiento-01-01"

        val request = RegisterRequestDTO(
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            correo = usuario.correo,
            contrasena = usuario.contrasena,
            fechaNacimiento = fechaNacimientoStr
        )

        // 1. Registro en el backend
        val response = ApiClient.authApi.registrar(request)

        // 2. Guardamos sesión completa (Token + ID + Datos)
        UserSession.token = response.token
        UserSession.idUsuario = response.idUsuario
        UserSession.nombreCompleto = response.nombreCompleto
        UserSession.correo = response.correo
        UserSession.rol = response.rol
    }


    /**
     * Verifica si ya existe un usuario con el correo dado en el backend.
     * Si existe, devolvemos una RegistroUsuarioEntity “armada” desde el DTO.
     */
    open suspend fun verificarCorreo(correo: String): RegistroUsuarioEntity? {
        val dto = remote.buscarPorCorreo(correo)
        return dto?.let { dtoToEntity(it) }
    }

    // ---------- Resto de métodos que tenía el repo original ----------

    suspend fun eliminarPorCorreo(correo: String) {
        val dto = remote.buscarPorCorreo(correo)
        val id = dto?.id
        if (id != null) {
            remote.eliminarUsuario(id)
        }
    }

    /**
     * Devuelve un flujo con todos los usuarios (mapeados desde el backend).
     */
    fun obtenerUsuarios(): Flow<List<RegistroUsuarioEntity>> = flow {
        val listaDto = remote.obtenerUsuarios()
        val listaEntity = listaDto.map { dtoToEntity(it) }
        emit(listaEntity)
    }

    /**
     * Devuelve un flujo con el usuario cuyo id coincida (o null).
     */
    fun obtenerUsuarioPorId(id: Int): Flow<RegistroUsuarioEntity?> = flow {
        val dto = remote.obtenerUsuario(id.toLong())
        emit(dtoToEntity(dto))
    }

    /**
     * Actualiza los datos de un usuario existente en el backend.
     */
    suspend fun actualizarUsuario(usuario: RegistroUsuarioEntity) {
        val id = usuario.id.toLong()
        val dto = UsuarioDTO(
            id = id,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            correo = usuario.correo,
            contrasena = usuario.contrasena,
            edad = usuario.edad,
            admin = false
        )
        remote.actualizarUsuario(id, dto)
    }

    /**
     * Elimina un usuario por su id en el backend.
     */
    suspend fun eliminarUsuario(id: Int) {
        remote.eliminarUsuario(id.toLong())
    }
}
