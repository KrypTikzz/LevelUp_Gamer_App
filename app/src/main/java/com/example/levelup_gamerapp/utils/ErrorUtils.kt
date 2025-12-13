package com.example.levelup_gamerapp.utils

/**
 * Utilidad para traducir códigos HTTP en mensajes más comprensibles para el usuario.
 *
 * Se puede pasar un nombre de recurso (por ejemplo "Usuario", "Producto", "Categoría")
 * para personalizar el mensaje en caso de 404 ("Usuario no encontrado", etc.).
 */
object ErrorUtils {
    /**
     * Traduce un código HTTP en una explicación amigable.
     *
     * @param codigo Código de estado HTTP devuelto por la API.
     * @param recurso Nombre del recurso afectado (opcional).
     * @return Texto descriptivo del problema.
     */
    fun traducirCodigoHTTP(codigo: Int, recurso: String? = null): String {
        return when (codigo) {
            400 -> "Solicitud inválida. Verifica los datos enviados."
            401 -> "No autorizado. Credenciales incorrectas o token expirado."
            403 -> "Acceso denegado. No tienes permisos suficientes."
            404 -> recurso?.let { "$it no encontrado" } ?: "Recurso no encontrado"
            409 -> "Conflicto en los datos enviados. Puede que el recurso ya exista."
            500 -> "Error interno del servidor. Intenta nuevamente más tarde."
            else -> "Se produjo un error inesperado ($codigo)."
        }
    }
}
