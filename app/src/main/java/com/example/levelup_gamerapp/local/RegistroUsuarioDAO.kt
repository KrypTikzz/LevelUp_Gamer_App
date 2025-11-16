package com.example.levelup_gamerapp.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RegistroUsuarioDAO {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertarUsuario(usuario: RegistroUsuarioEntity)

    @Query("SELECT * FROM levelup_database WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): RegistroUsuarioEntity?

    @Query("DELETE FROM levelup_database WHERE correo = :correo")
    suspend fun eliminarPorCorreo(correo: String)

    /**
     * Obtiene todos los usuarios registrados en la base de datos.
     * Se devuelve un Flow para que la UI pueda reaccionar ante cambios.
     */
    @Query("SELECT * FROM levelup_database")
    fun obtenerTodosUsuarios(): kotlinx.coroutines.flow.Flow<List<RegistroUsuarioEntity>>

    /**
     * Busca un usuario por su identificador. Devuelve un Flow que emitirá null si no existe.
     */
    @Query("SELECT * FROM levelup_database WHERE id = :id LIMIT 1")
    fun obtenerUsuarioPorId(id: Int): kotlinx.coroutines.flow.Flow<RegistroUsuarioEntity?>

    /**
     * Actualiza los datos de un usuario existente. Se basa en el id primario de la entidad.
     */
    @Update
    suspend fun actualizarUsuario(usuario: RegistroUsuarioEntity)

    /**
     * Elimina un usuario por su identificador.
     */
    @Query("DELETE FROM levelup_database WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}