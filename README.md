# Level-Up Gamer App

## 1. Nombre del proyecto
**Level-Up Gamer App** 

## 2. Integrantes
- Rodrigo Rojas  
- Luciano Machuca

## 3. Funcionalidades
- Registro e inicio de sesión de usuarios con validaciones (correo, contraseña, edad).
- Descuento especial para correos `@duocuc.cl`.
- Creacion de cuenta admin con el correo `admin@levelup.cl`.
- Listado y detalle de productos (nombre, descripción, precio, imagen, categoría).
- Carrito de compras: agregar, eliminar y vaciar productos.
- Administración básica de productos y usuarios (crear / editar / eliminar) para rol administrador.

## 3. Endpoints
Endpoints de productos

| Ruta                                     | Método     | Descripción                                                                   |
| ---------------------------------------- | ---------- | ----------------------------------------------------------------------------- |
| `/api/productos`                         | **GET**    | Devuelve todos los productos disponibles.                                     |
| `/api/productos/{id}`                    | **GET**    | Devuelve un producto por su ID.                                               |
| `/api/productos/categoria/{categoriaId}` | **GET**    | Devuelve los productos de una categoría específica.                           |
| `/api/productos`                         | **POST**   | Crea un nuevo producto con los datos enviados en el cuerpo de la petición.    |
| `/api/productos/{id}`                    | **PUT**    | Actualiza un producto existente (ID) con los datos del cuerpo de la petición. |
| `/api/productos/{id}`                    | **DELETE** | Elimina un producto por su ID.                                                |

Endpoints de categorías

| Ruta                              | Método     | Descripción                                                                     |
| --------------------------------- | ---------- | ------------------------------------------------------------------------------- |
| `/api/usuarios`                   | **GET**    | Devuelve la lista de todos los usuarios.                                        |
| `/api/usuarios/{id}`              | **GET**    | Devuelve un usuario por su ID.                                                  |
| `/api/usuarios`                   | **POST**   | Crea un nuevo usuario (requiere los datos de usuario en el cuerpo).             |
| `/api/usuarios/{id}`              | **PUT**    | Actualiza un usuario existente.                                                 |
| `/api/usuarios/{id}`              | **DELETE** | Elimina un usuario por su ID.                                                   |
| `/api/usuarios/buscar?correo=...` | **GET**    | Busca un usuario por su correo electrónico.                                     |
| `/api/usuarios/login`             | **POST**   | Inicia sesión; recibe el correo y la contraseña como parámetros de la petición. |

Endpoints de usuarios

| Ruta                              | Método     | Descripción                                                                     |
| --------------------------------- | ---------- | ------------------------------------------------------------------------------- |
| `/api/usuarios`                   | **GET**    | Devuelve la lista de todos los usuarios.                                        |
| `/api/usuarios/{id}`              | **GET**    | Devuelve un usuario por su ID.                                                  |
| `/api/usuarios`                   | **POST**   | Crea un nuevo usuario (requiere los datos de usuario en el cuerpo).             |
| `/api/usuarios/{id}`              | **PUT**    | Actualiza un usuario existente.                                                 |
| `/api/usuarios/{id}`              | **DELETE** | Elimina un usuario por su ID.                                                   |
| `/api/usuarios/buscar?correo=...` | **GET**    | Busca un usuario por su correo electrónico.                                     |
| `/api/usuarios/login`             | **POST**   | Inicia sesión; recibe el correo y la contraseña como parámetros de la petición. |

## 5. Pasos para ejecutar

1. **Levantar backend**  
   - Ejecutar el proyecto backend .  

2. **Ejecutar la app Android**  
   - Abrir este proyecto en Android Studio.  
   - Sincronizar Gradle.  
   - Ejecutar en emulador

## 6. Captura apk firmada + key

<img width="260" height="121" alt="image" src="https://github.com/user-attachments/assets/c3b970f3-c676-41a7-9882-bc47d1911d16" />




