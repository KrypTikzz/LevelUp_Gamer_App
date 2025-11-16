package com.example.levelup_gamerapp.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interface de Retrofit que define todas las llamadas al backend de LevelUp Gamer.
 * Se añaden métodos para listar y crear productos, categorías y usuarios,
 * además del ya existente para crear pedidos. Cada llamada devuelve un
 * `Response` cuando es necesario comprobar el código HTTP o, en caso de
 * colecciones sencillas, se devuelve la lista directamente.
 */
interface LevelUpApi {
    /**
     * Registra un nuevo pedido en el backend. Devuelve un [Response] para
     * permitir comprobar si la operación fue exitosa a nivel HTTP.
     */
    @POST("api/pedidos")
    suspend fun crearPedido(@Body request: CrearPedidoRequest): Response<Unit>

    /**
     * Obtiene la lista completa de productos disponibles en el backend.
     */
    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoDTO>

    /**
     * Crea un nuevo producto en el backend y devuelve el producto creado.
     */
    @POST("api/productos")
    suspend fun crearProducto(@Body producto: ProductoDTO): Response<ProductoDTO>

    /**
     * Elimina un producto por su identificador. Devuelve una respuesta vacía
     * con el código HTTP de la operación.
     */
    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>

    /**
     * Obtiene todas las categorías existentes en el backend.
     */
    @GET("api/categorias")
    suspend fun obtenerCategorias(): List<CategoriaDTO>

    /**
     * Crea una nueva categoría y devuelve la categoría resultante.
     */
    @POST("api/categorias")
    suspend fun crearCategoria(@Body categoria: CategoriaDTO): Response<CategoriaDTO>

    /**
     * Obtiene todos los usuarios registrados en el backend.
     */
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioDTO>

    /**
     * Registra un nuevo usuario en el backend y devuelve el usuario creado.
     */
    @POST("api/usuarios")
    suspend fun crearUsuario(@Body usuario: UsuarioDTO): Response<UsuarioDTO>
}
