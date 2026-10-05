# 🍗 SaborApp — Pollería El Buen Sabor

Aplicación móvil nativa para Android desarrollada en **Kotlin** para la gestión integral de pedidos, mesas, carta de platos y reportes de un restaurante/pollería.

---

## 📌 Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **Plataforma:** Android Nativo (minSdk 24, targetSdk 37)
- **Interfaz Gráfica:** XML + Material Components 3 + ViewBinding
- **Arquitectura:** MVVM Ligero / Repository Pattern con Kotlin Coroutines
- **Conectividad & Red:** Retrofit 2 + Gson Converter + OkHttp Logging Interceptor
- **Base de Datos:** MySQL administrado mediante XAMPP / phpMyAdmin
- **Backend:** REST API en PHP (PDO con soporte JSON)

---

## 🚀 Sprint 1 — App Navegable y Autenticación

### Objetivos Logrados:
1. **Infraestructura de Base de Datos:**
   - Script SQL [`database/saborapp.sql`](database/saborapp.sql) con las 5 tablas: `usuario`, `plato`, `mesa`, `pedido`, `detalle_pedido`.
   - Relaciones de clave foránea e integridad referencial (`ON DELETE CASCADE` en detalles).
   - Usuarios iniciales de prueba:
     - `admin` / `1234` (Rol: `ADMIN`)
     - `mozo1` / `1234` (Rol: `MOZO`)
2. **HU-01 (Login):**
   - Validación de campos vacíos con errores en tiempo real (`TextInputLayout`).
   - Autenticación asíncrona contra backend MySQL con resiliencia de red.
   - Ocultamiento de contraseña con botón toggle interactivo (ojo).
   - Manejo de sesión y limpieza de la pila de navegación al ingresar.
3. **HU-02 (Menú Principal y Navegación):**
   - Tarjetas interactivas para: Platos, Mesas, Pedidos, Reportes y Salir.
   - Navegación fluida a las actividades base.
   - Control de visibilidad por roles: el rol `MOZO` no visualiza `Reportes`, mientras que `ADMIN` tiene acceso total.
   - Cierre de sesión seguro (`SessionManager`).
4. **HU-03 (Identidad Visual):**
   - Paleta corporativa cálida (Naranja `#E65100` y Rojo `#D32F2F`).
   - Iconografía y logo temático de pollería.
   - 100% de textos centralizados en `strings.xml`.

---

## 🛠️ Configuración y Despliegue Local

1. Iniciar **Apache** y **MySQL** desde el Panel de Control de **XAMPP**.
2. Crear la base de datos `saborapp` e importar el archivo `database/saborapp.sql`.
3. Copiar la carpeta `backend_api` en `C:\xampp\htdocs\saborapp_api\`.
4. Abrir el proyecto en **Android Studio** y ejecutar en el emulador o dispositivo físico.

