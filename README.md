# 🍗 SaborApp — Pollería El Buen Sabor

Aplicación móvil nativa para Android desarrollada en **Kotlin** para la gestión integral de un restaurante/pollería (carta de platos, mesas, pedidos, reportes y autenticación con control de roles).

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

## 🚀 SPRINT 1 — Infraestructura, Login y Menú Navegable

### Historias de Usuario:
- **HU-01 (Login):**
  - Validación de campos vacíos en tiempo real (`TextInputLayout`).
  - Autenticación asíncrona y bloqueo de retroceso al Login tras iniciar sesión.
  - Ocultamiento de contraseña con toggle interactivo (ojo).
- **HU-02 (Menú Principal y Navegación):**
  - Tarjetas interactivas para: Platos, Mesas, Pedidos, Reportes y Salir.
  - Control de visibilidad por roles: el rol `MOZO` no visualiza `Reportes`, mientras que `ADMIN` tiene acceso total.
  - Cierre de sesión seguro con `SessionManager`.
- **HU-03 (Identidad Visual):**
  - Paleta corporativa cálida (Naranja `#E65100` y Rojo `#D32F2F`).
  - Iconografía y logo temático de pollería.
  - 100% de textos centralizados en `strings.xml`.

---

## 🚀 SPRINT 2 — Datos Reales: Platos, Mesas y Login contra MySQL

### Historias de Usuario:
- **HU-04 (Base de Datos y Login Real):**
  - Autenticación 100% real consultando la tabla `usuario` en la base de datos `saborapp` de MySQL.
  - Usuarios iniciales:
    - `admin` / `1234` (Rol: `ADMIN`)
    - `mozo1` / `1234` (Rol: `MOZO`)
- **HU-05 (Registrar y Listar Platos):**
  - Listado de la carta en `RecyclerView` ordenado por **Categoría** y **Nombre**.
  - **Filtro interactivo en tiempo real** por categorías: *Todos, Entradas, Fondos, Bebidas, Postres*.
  - Formulario de registro (`FormularioPlatoActivity`):
    - Validación de nombre y precio obligatorios.
    - Validación de precio `> 0` (*"Precio inválido"*).
    - Selección de categoría mediante Spinner.
    - Persistencia en MySQL (`plato`).
- **HU-06 (Registrar y Listar Mesas):**
  - Listado de mesas en `RecyclerView` con `GridLayoutManager` de **3 columnas**.
  - Tarjetas con icono, número de mesa, capacidad y badge de estado (`LIBRE` / `OCUPADA`).
  - Diálogo de registro de mesas:
    - Validación de número duplicado (*"La mesa ya existe"*).
    - Validación de capacidad permitida entre **1 y 12 personas** (*"Capacidad inválida"*).
    - Estado inicial automático `LIBRE`.
    - Persistencia en MySQL (`mesa`).

---

## 🗄️ Modelo de Base de Datos (`saborapp`)

1. **`usuario`**: `id` (PK AUTO_INCREMENT), `usuario` (UNIQUE), `clave`, `rol` (`ADMIN` | `MOZO`), `fecha_creacion`.
2. **`plato`**: `id` (PK AUTO_INCREMENT), `nombre`, `categoria`, `precio` (> 0), `disponible`, `fecha_creacion`.
3. **`mesa`**: `id` (PK AUTO_INCREMENT), `numero` (UNIQUE), `capacidad`, `estado` (`LIBRE` | `OCUPADA`), `fecha_creacion`.
4. **`pedido`**: `id` (PK AUTO_INCREMENT), `id_mesa` (FK), `fecha`, `estado` (`ABIERTO` | `CERRADO`), `total`.
5. **`detalle_pedido`**: `id` (PK AUTO_INCREMENT), `id_pedido` (FK CASCADE), `id_plato` (FK), `cantidad`, `precio_unit`, `subtotal`.

---

## 🛠️ Configuración y Despliegue Local

1. Iniciar **Apache** y **MySQL** desde el Panel de Control de **XAMPP**.
2. Importar el script [`database/saborapp.sql`](database/saborapp.sql) en MySQL / phpMyAdmin.
3. Copiar la carpeta `backend_api` a `C:\xampp\htdocs\saborapp_api\`.
4. Abrir el proyecto en **Android Studio** y ejecutar (`Run` o `Shift + F10`).
