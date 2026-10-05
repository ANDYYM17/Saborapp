# SaborApp — Pollería El Buen Sabor 🍗

Aplicación móvil nativa en Android (Kotlin) para la gestión inteligente de pedidos, mesas, carta y reportes de un restaurante/pollería.

---

## 🚀 Sprint 1: App Navegable & Infraestructura Base
- **Base de Datos MySQL (XAMPP):** Script SQL con 5 tablas (`usuario`, `plato`, `mesa`, `pedido`, `detalle_pedido`).
- **Backend REST API:** Endpoints en PHP (`saborapp_api`).
- **HU-01 (Login):** Validaciones en tiempo real, toggle de visibilidad de contraseña y control de acceso.
- **HU-02 (Menú Principal & Navegación):** Tarjetas interactivas con control de roles (`ADMIN` vs `MOZO`).
- **HU-03 (Identidad Visual):** Paleta cálida (Naranja/Rojo), tema Material 3, ViewBinding y recursos externalizados.

---

## 👥 Credenciales de Prueba
- **Administrador:** `admin` / `1234` (Rol: `ADMIN` - Acceso completo)
- **Mozo:** `mozo1` / `1234` (Rol: `MOZO` - Reportes restringidos)

---

## 🛠️ Tecnologías
- **Lenguaje:** Kotlin
- **Arquitectura:** MVVM ligero / Repository Pattern
- **UI:** XML + ViewBinding + Material Components
- **Red:** Retrofit 2 + OkHttp + Coroutines
- **Base de datos:** MySQL (XAMPP / phpMyAdmin)
