# 🍗 SaborApp — Pollería El Buen Sabor

Aplicación móvil nativa en **Android (Kotlin)** para la gestión integral de pedidos, mesas, carta de platos y reportes en el restaurante / pollería **El Buen Sabor**.

---

## 🚀 Arquitectura y Tecnologías
- **Lenguaje:** Kotlin
- **Plataforma:** Android Nativo (SDK 24+)
- **UI:** Material Components + XML + ViewBinding
- **Concurrencia:** Kotlin Coroutines & Lifecycle KTX
- **Networking:** Retrofit 2 + Gson Converter + OkHttp Logging Interceptor
- **Base de Datos:** MySQL (XAMPP / phpMyAdmin)
- **Backend API:** PHP REST API (PDO)

---

## 📁 Estructura del Repositorio
```
Saborapp/
├── app/                  # Código fuente de la aplicación Android
├── backend_api/          # Endpoints REST API en PHP para XAMPP
├── database/             # Script SQL con DDL y datos iniciales (saborapp.sql)
└── README.md
```

---

## 👥 Usuarios de Demostración (Sprint 1)
- **Administrador:** `usuario: admin` / `clave: 1234` (Rol: `ADMIN`)
- **Mozo:** `usuario: mozo1` / `clave: 1234` (Rol: `MOZO`)

---

## 📌 Sprints del Proyecto
- [x] **Sprint 1:** App navegable (Login validado con MySQL, Menú principal con roles ADMIN/MOZO, Identidad visual naranja/rojo y pantallas base).
- [ ] **Sprint 2:** Gestión de Platos (CRUD completo y carta).
- [ ] **Sprint 3:** Gestión de Mesas y Toma de Pedidos.
- [ ] **Sprint 4:** Cierre de cuenta, Reportes y Compartir por WhatsApp.
