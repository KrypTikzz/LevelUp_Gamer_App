package com.example.levelup_gamerapp.remote

import retrofit2.Response
import retrofit2.http.*

/**
 * Declaración de todas las rutas que expone el backend
 */
interface LevelUpApi {

    // ---------- Productos ----------

    /** Obtiene el listado completo de productos. */
    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoDTO>

    /** Obtiene los detalles de un producto concreto. */
    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoDTO

    /** Crea un nuevo producto en el backend. */
    @POST("api/productos")
    suspend fun crearProducto(@Body producto: ProductoDTO): Response<ProductoDTO>

    /** Actualiza un producto existente. */
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoDTO
    ): Response<ProductoDTO>

    /** Elimina un producto por su identificador. */
    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>



    // ---------- Categorías ----------

    /** Devuelve todas las categorías disponibles. */
    @GET("api/categorias")
    suspend fun obtenerCategorias(): List<CategoriaDTO>

    /** Crea una nueva categoría. */
    @POST("api/categorias")
    suspend fun crearCategoria(@Body categoria: CategoriaDTO): Response<CategoriaDTO>

    /** Actualiza una categoría existente. */
    @PUT("api/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body categoria: CategoriaDTO
    ): Response<CategoriaDTO>

    /** Elimina una categoría por su identificador. */
    @DELETE("api/categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Long): Response<Unit>

    // ---------- Usuarios ----------

    /** Obtiene el listado completo de usuarios. */
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioDTO>

    /** Obtiene los detalles de un usuario. */
    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Long): UsuarioDTO

    /** Crea un usuario. */
    @POST("api/usuarios")
    suspend fun crearUsuario(@Body usuario: UsuarioDTO): Response<UsuarioDTO>

    /** Actualiza un usuario existente. */
    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioDTO
    ): Response<UsuarioDTO>

    /** Elimina un usuario por su identificador. */
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>

    /** Busca un usuario por su correo electrónico. */
    @GET("api/usuarios/buscar")
    suspend fun buscarUsuarioPorCorreo(@Query("correo") correo: String): UsuarioDTO?

    /** Realiza el login de un usuario. */
    @POST("api/usuarios/login")
    suspend fun login(
        @Query("correo") correo: String,
        @Query("contrasena") contrasena: String
    ): UsuarioDTO?

    // ---------- Pedidos ----------

    /** Registra un nuevo pedido. */
    @POST("api/pedidos")
    suspend fun crearPedido(@Body request: CrearPedidoRequest): retrofit2.Response<PedidoResponseDTO>

    /** Historial de compras del usuario logueado (cliente). */
    @GET("api/pedidos/usuario/{usuarioId}")
    suspend fun listarPedidosPorUsuario(@Path("usuarioId") usuarioId: Long): List<PedidoResponseDTO>

    /** Obtener un pedido por ID (por si después quieres detalle directo). */
    @GET("api/pedidos/{id}")
    suspend fun obtenerPedido(@Path("id") id: Long): PedidoResponseDTO

    /** Listar todos (útil si después lo quieres para admin). */
    @GET("api/pedidos")
    suspend fun listarPedidos(): List<PedidoResponseDTO>













}
