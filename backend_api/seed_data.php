<?php
// ==========================================================
// SaborApp - Script de Carga de Datos de Prueba (Seed Data)
// ==========================================================

require_once __DIR__ . '/config/database.php';

$database = new Database();
$db = $database->getConnection();

if (!$db) {
    die("Error al conectar a MySQL\n");
}

echo "--> Conectado exitosamente a MySQL (Base: saborapp)\n\n";

// 1. Verificar usuarios existentes (NO se agregan más usuarios por indicación del usuario)
$uStmt = $db->query("SELECT id, usuario, rol FROM usuario");
$usuarios = $uStmt->fetchAll(PDO::FETCH_ASSOC);
echo "1. USUARIOS ACTUALES (se mantienen intactos):\n";
foreach ($usuarios as $u) {
    echo "   - ID: {$u['id']} | Usuario: {$u['usuario']} | Rol: {$u['rol']}\n";
}
echo "\n";

// 2. Limpiar tablas de pedidos y detalle_pedido para repoblar con datos consistentes
$db->exec("SET FOREIGN_KEY_CHECKS = 0;");
$db->exec("TRUNCATE TABLE detalle_pedido;");
$db->exec("TRUNCATE TABLE pedido;");
$db->exec("TRUNCATE TABLE plato;");
$db->exec("TRUNCATE TABLE mesa;");
$db->exec("SET FOREIGN_KEY_CHECKS = 1;");

echo "2. Tablas platos, mesas, pedido y detalle_pedido limpiadas para inserción de datos frescos.\n";

// 3. Insertar Carta de Platos
$platos = [
    // Fondos
    ['1/4 Pollo a la Brasa con Papas y Ensalada', 'Fondos', 22.50, 1],
    ['1/2 Pollo a la Brasa con Papas y Ensalada', 'Fondos', 42.00, 1],
    ['1 Pollo a la Brasa Entero Familiar', 'Fondos', 78.00, 1],
    ['Mostrito Criollo Clásico', 'Fondos', 25.00, 1],
    ['Lomo Saltado a la Leña', 'Fondos', 34.00, 1],
    ['Chaufa de Pollo Especial', 'Fondos', 20.00, 1],
    
    // Entradas
    ['Anticuchos de Corazón (3 Palos)', 'Entradas', 24.00, 1],
    ['Papa a la Huancaína Tradicional', 'Entradas', 16.00, 1],
    ['Tequeños de Queso con Guacamole', 'Entradas', 18.00, 1],
    ['Porción de Alitas Broaster (6 und)', 'Entradas', 22.00, 1],
    
    // Bebidas
    ['Jarra de Chicha Morada Casera (1L)', 'Bebidas', 12.00, 1],
    ['Jarra de Maracuyá Natural (1L)', 'Bebidas', 12.00, 1],
    ['Gaseosa Inka Kola 1.5L', 'Bebidas', 10.00, 1],
    ['Gaseosa Coca Cola 1.5L', 'Bebidas', 10.00, 1],
    ['Cerveza Cusqueña Trigo 620ml', 'Bebidas', 14.00, 1],
    
    // Postres
    ['Combinado: Mazamorra + Arroz con Leche', 'Postres', 9.50, 1],
    ['Torta de Chocolate Húmeda', 'Postres', 11.00, 1],
    ['Suspiro a la Limeña Tradicional', 'Postres', 10.00, 0] // No disponible para probar filtro CA4
];

$stmtPlato = $db->prepare("INSERT INTO plato (nombre, categoria, precio, disponible) VALUES (?, ?, ?, ?)");
$platoIds = [];
foreach ($platos as $p) {
    $stmtPlato->execute($p);
    $platoIds[$p[0]] = (int)$db->lastInsertId();
}
echo "3. " . count($platos) . " platos registrados en la carta.\n";

// 4. Insertar Mesas
$mesas = [
    [1, 4, 'LIBRE'],
    [2, 4, 'OCUPADA'], // Mesa 2 con pedido activo
    [3, 2, 'LIBRE'],
    [4, 6, 'LIBRE'],
    [5, 8, 'LIBRE'],
    [6, 4, 'LIBRE']
];

$stmtMesa = $db->prepare("INSERT INTO mesa (numero, capacidad, estado) VALUES (?, ?, ?)");
$mesaIds = [];
foreach ($mesas as $m) {
    $stmtMesa->execute($m);
    $mesaIds[$m[0]] = (int)$db->lastInsertId();
}
echo "4. " . count($mesas) . " mesas registradas en el salón.\n";

// 5. Insertar Pedidos Históricos Cerrados de Hoy (para reportes de ventas HU-10)
$fechaHoy = date('Y-m-d H:i:s');

// Pedido 1 (Mesa 1 - CERRADO)
$stmtPed = $db->prepare("INSERT INTO pedido (id_mesa, fecha, estado, total) VALUES (?, ?, ?, ?)");
$stmtDet = $db->prepare("INSERT INTO detalle_pedido (id_pedido, id_plato, cantidad, precio_unit, subtotal) VALUES (?, ?, ?, ?, ?)");

// Venta 1: Mesa 1
$stmtPed->execute([$mesaIds[1], date('Y-m-d 11:30:00'), 'CERRADO', 102.50]);
$idPed1 = $db->lastInsertId();
$stmtDet->execute([$idPed1, $platoIds['1 Pollo a la Brasa Entero Familiar'], 1, 78.00, 78.00]);
$stmtDet->execute([$idPed1, $platoIds['Jarra de Chicha Morada Casera (1L)'], 1, 12.00, 12.00]);
$stmtDet->execute([$idPed1, $platoIds['Combinado: Mazamorra + Arroz con Leche'], 1, 9.50, 9.50]);
$stmtDet->execute([$idPed1, $platoIds['Tequeños de Queso con Guacamole'], 1, 18.00, 18.00]);
// recalculando total exacto: 78 + 12 + 9.5 + 18 = 117.50
$db->query("UPDATE pedido SET total = 117.50 WHERE id = $idPed1");

// Venta 2: Mesa 3
$stmtPed->execute([$mesaIds[3], date('Y-m-d 12:05:00'), 'CERRADO', 57.00]);
$idPed2 = $db->lastInsertId();
$stmtDet->execute([$idPed2, $platoIds['1/4 Pollo a la Brasa con Papas y Ensalada'], 2, 22.50, 45.00]);
$stmtDet->execute([$idPed2, $platoIds['Jarra de Maracuyá Natural (1L)'], 1, 12.00, 12.00]);
$db->query("UPDATE pedido SET total = 57.00 WHERE id = $idPed2");

// Venta 3: Mesa 4
$stmtPed->execute([$mesaIds[4], date('Y-m-d 12:45:00'), 'CERRADO', 126.00]);
$idPed3 = $db->lastInsertId();
$stmtDet->execute([$idPed3, $platoIds['Lomo Saltado a la Leña'], 2, 34.00, 68.00]);
$stmtDet->execute([$idPed3, $platoIds['Anticuchos de Corazón (3 Palos)'], 1, 24.00, 24.00]);
$stmtDet->execute([$idPed3, $platoIds['Torta de Chocolate Húmeda'], 2, 11.00, 22.00]);
$stmtDet->execute([$idPed3, $platoIds['Jarra de Chicha Morada Casera (1L)'], 1, 12.00, 12.00]);
$db->query("UPDATE pedido SET total = 126.00 WHERE id = $idPed3");

// Venta 4: Mesa 5
$stmtPed->execute([$mesaIds[5], date('Y-m-d 13:15:00'), 'CERRADO', 190.00]);
$idPed4 = $db->lastInsertId();
$stmtDet->execute([$idPed4, $platoIds['1 Pollo a la Brasa Entero Familiar'], 2, 78.00, 156.00]);
$stmtDet->execute([$idPed4, $platoIds['Porción de Alitas Broaster (6 und)'], 1, 22.00, 22.00]);
$stmtDet->execute([$idPed4, $platoIds['Gaseosa Inka Kola 1.5L'], 1, 10.00, 10.00]);
$stmtDet->execute([$idPed4, $platoIds['Gaseosa Coca Cola 1.5L'], 1, 10.00, 10.00]);
$db->query("UPDATE pedido SET total = 198.00 WHERE id = $idPed4");

echo "5. 4 pedidos cerrados de hoy insertados con sus respectivos consumos.\n";

// 6. Insertar Pedido ABIERTO Activo en Mesa 2 (para probar Pedidos y Ver Cuenta)
$stmtPed->execute([$mesaIds[2], date('Y-m-d H:i:s'), 'ABIERTO', 54.00]);
$idPedActivo = $db->lastInsertId();
$stmtDet->execute([$idPedActivo, $platoIds['1/2 Pollo a la Brasa con Papas y Ensalada'], 1, 42.00, 42.00]);
$stmtDet->execute([$idPedActivo, $platoIds['Jarra de Chicha Morada Casera (1L)'], 1, 12.00, 12.00]);
$db->query("UPDATE pedido SET total = 54.00 WHERE id = $idPedActivo");

echo "6. 1 pedido ABIERTO en Mesa 2 listo para atención y cobro en la app.\n\n";

echo "=== CARGA DE DATOS COMPLETADA CON ÉXITO ===\n";
?>
