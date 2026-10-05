-- ==========================================================
-- SaborApp - Base de Datos MySQL
-- Pollería El Buen Sabor
-- Sprint 1: Script de Creación de Tablas y Datos Iniciales
-- ==========================================================

CREATE DATABASE IF NOT EXISTS saborapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE saborapp;

-- 1. Tabla: usuario
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS mesa;
DROP TABLE IF EXISTS plato;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'MOZO') NOT NULL DEFAULT 'MOZO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Tabla: plato
CREATE TABLE plato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    disponible TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabla: mesa
CREATE TABLE mesa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    estado ENUM('LIBRE', 'OCUPADA') NOT NULL DEFAULT 'LIBRE',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabla: pedido
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ABIERTO', 'CERRADO') NOT NULL DEFAULT 'ABIERTO',
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_pedido_mesa FOREIGN KEY (id_mesa) REFERENCES mesa(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Tabla: detalle_pedido
CREATE TABLE detalle_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_plato INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unit DECIMAL(10,2) NOT NULL CHECK (precio_unit >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_plato FOREIGN KEY (id_plato) REFERENCES plato(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- Inserción de Datos Iniciales para Sprint 1
-- ==========================================================

-- Usuarios de prueba iniciales
INSERT INTO usuario (usuario, clave, rol) VALUES 
('admin', '1234', 'ADMIN'),
('mozo1', '1234', 'MOZO');

-- Platos base de ejemplo para la Pollería El Buen Sabor
INSERT INTO plato (nombre, categoria, precio, disponible) VALUES
('1/4 de Pollo a la Brasa', 'Pollos', 22.50, 1),
('1/2 Pollo a la Brasa', 'Pollos', 42.00, 1),
('1 Pollo a la Brasa Entero', 'Pollos', 78.00, 1),
('Porción de Papas Fritas', 'Guarniciones', 12.00, 1),
('Ensalada Clásica', 'Guarniciones', 8.50, 1),
('Inca Kola 1.5L', 'Bebidas', 11.00, 1),
('Chicha Morada Jarra 1L', 'Bebidas', 14.00, 1);

-- Mesas base de ejemplo
INSERT INTO mesa (numero, capacidad, estado) VALUES
(1, 4, 'LIBRE'),
(2, 4, 'LIBRE'),
(3, 2, 'LIBRE'),
(4, 6, 'LIBRE'),
(5, 8, 'LIBRE');

