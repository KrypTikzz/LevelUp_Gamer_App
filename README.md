LevelUp Gamer es una aplicación móvil desarrollada en Kotlin con Jetpack Compose, conectada a un backend en Spring Boot, orientada a la venta de productos gamer.
Permite gestionar usuarios, productos, carrito de compras, boletas y opiniones, diferenciando funcionalidades según el rol del usuario.

Tecnologías Utilizadas

Frontend

 Kotlin
 Jetpack Compose
 Arquitectura MVVM
 Retrofit
 Coroutines
 Room
 DataStore

Backend
 Spring Boot
 Spring Security + JWT
 PostgreSQL
 JPA / Hibernate
 Swagger (OpenAPI)
 AWS (RDS)

Otros
 GitHub
 APK firmado + archivo

Instalación

Android
Clonar repositorio
Abrir en Android Studio
Sincronizar Gradle
Ejecutar en emulador o dispositivo
APK firmado y .jks incluidos en el repositorio

Backend
Clonar repositorio
Abrir en IntelliJ IDEA
Configurar application.properties
Ejecutar Spring Boot

Funcionalidades

Registro e inicio de sesión con JWT
Listado y detalle de productos
Opiniones por producto
Carrito de compras con persistencia local
Checkout y generación de boletas
Historial de compras
Gestión de productos, categorías y noticias (admin)

Endpoints Principales

Auth: /api/v1/auth/login, /api/v1/auth/registro

Productos: /api/v1/productos

Opiniones: /api/v1/productos/{idProducto}/opiniones

Categorías: /api/v1/categorias

Noticias: /api/v1/noticias

Boletas: /api/v1/boletas


