#  SaborApp — Sistema Integral de Gestión de Restaurante

<p align="center">
  <b>Pollería "El Buen Sabor"</b><br>
  Aplicación Android Nativa (Kotlin) conectada con Backend REST PHP y Base de Datos MySQL (XAMPP).
</p>

---

##  Resumen de Sprints y Cobertura Scrum

| Sprint | Historias de Usuario | Puntos | Estado | Objetivo |
| :--- | :--- | :---: | :---: | :--- |
| **Sprint 1** | **HU-01**, **HU-02**, **HU-03** | 8 | ✅ Validado | Prototipo visual, navegación entre módulos y control de roles (`ADMIN` / `MOZO`). |
| **Sprint 2** | **HU-04**, **HU-05**, **HU-06** | 11 | ✅ Completado | Persistencia real en MySQL: Login real, CRUD platos (registro y listado), registro y grilla de mesas. |
| **Sprint 3** | **HU-07**, **HU-08**, **HU-09** | 16 | ✅ Completado | Edición/eliminación y búsqueda en tiempo real de platos; toma de pedidos por mesa; detalle y cierre transaccional de cuentas. |
| **Sprint 4** | **HU-10**, **HU-11**, **HU-12** | 14 | ✅ Completado | Reportes de ventas (Venta del día, Top 5 platos, Ventas por mesa); compartir cuenta por WhatsApp; sesión recordada y APK Release. |

---

##  Base de Datos (`saborapp` en MySQL / phpMyAdmin)

La base de datos se aloja en MySQL mediante **XAMPP**:
- **Host:** `localhost:3306` (o IP local de la red)
- **Base de Datos:** `saborapp`
- **Usuario:** `root`
- **Contraseña:** *(vacía)*

### Tablas y Estructura Relacional
1. **`usuario`**: `id` (PK, AI), `usuario` (UNIQUE), `clave`, `rol` (`ADMIN` | `MOZO`).
2. **`plato`**: `id` (PK, AI), `nombre`, `categoria`, `precio` (DECIMAL), `disponible` (0/1).
3. **`mesa`**: `id` (PK, AI), `numero` (UNIQUE), `capacidad` (1-12), `estado` (`LIBRE` | `OCUPADA`).
4. **`pedido`**: `id` (PK, AI), `id_mesa` (FK -> mesa.id), `fecha` (DATETIME), `estado` (`ABIERTO` | `CERRADO`), `total` (DECIMAL).
5. **`detalle_pedido`**: `id` (PK, AI), `id_pedido` (FK -> pedido.id ON DELETE CASCADE), `id_plato` (FK -> plato.id), `cantidad`, `precio_unit` (DECIMAL snapshot), `subtotal` (DECIMAL).

---

##  Endpoints Backend REST API (`/saborapp_api/`)

- **Autenticación (HU-04 / HU-12):**
  - `POST /auth/login.php` -> Validación en tiempo real contra tabla `usuario`.
- **Platos (HU-05 / HU-07):**
  - `GET /platos/listar.php?search=&categoria=&solo_disponibles=` -> Listado y búsqueda `LIKE %search%`.
  - `POST /platos/registrar.php` -> Alta de plato.
  - `POST /platos/actualizar.php` -> Edición de plato.
  - `POST /platos/eliminar.php` -> Eliminación protegida (bloquea si tiene pedidos asociados en `detalle_pedido`).
- **Mesas (HU-06):**
  - `GET /mesas/listar.php` -> Lista de mesas con capacidad y estado.
  - `POST /mesas/registrar.php` -> Registro de nueva mesa (valida duplicados y capacidad de 1 a 12).
- **Pedidos y Cuentas (HU-08 / HU-09):**
  - `GET /pedidos/obtener_por_mesa.php?id_mesa=` -> Obtiene el pedido activo (`ABIERTO`) y detalle de ítems.
  - `POST /pedidos/agregar_item.php` -> Agrega ítems, congela `precio_unit` y cambia estado de mesa a `OCUPADA`.
  - `POST /pedidos/cerrar_cuenta.php` -> Cierra la cuenta (`pedido.estado = 'CERRADO'`), calcula total acumulado y libera la mesa (`mesa.estado = 'LIBRE'`).
- **Reportes de Ventas (HU-10):**
  - `GET /reportes/resumen.php` -> Venta total del día, Top 5 platos más pedidos y ventas agrupadas por mesa.

---

##  Credenciales de Acceso

| Usuario | Contraseña | Rol | Acceso a Módulos |
| :--- | :--- | :--- | :--- |
| **`admin`** | `1234` | `ADMIN` | Platos (CRUD completo), Mesas, Pedidos, Reportes, Salir |
| **`mozo`** | `1234` | `MOZO` | Platos (Consulta), Mesas, Pedidos (Toma y Cobro), Salir |

---

##  Compilación y Generación de APKs (HU-12)

- **APK Debug:** `app/build/outputs/apk/debug/app-debug.apk`
- **APK Release:** `app/build/outputs/apk/release/app-release-unsigned.apk`

Comandos de compilación Gradle:
```bash
# Compilar Debug APK
./gradlew assembleDebug

# Compilar Release APK
./gradlew assembleRelease
```

---

##  Ramas Git del Proyecto

- `main` -> Versión final consolidada y estable.
- `sprint1` -> Entregable Sprint 1 (Prototipos y navegación).
- `sprint2` -> Entregable Sprint 2 (Persistencia MySQL, login y listados).
- `sprint3` -> Entregable Sprint 3 (Edición de platos, toma de pedidos y cierre de cuentas).
- `sprint4` -> Entregable Sprint 4 (Reportes, WhatsApp, sesión persistente y Release APK).
