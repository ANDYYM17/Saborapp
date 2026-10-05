<?php
// ==========================================================
// SaborApp - Backend REST API
// Reportes de Ventas y Estadísticas (HU-10)
// ==========================================================

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, OPTIONS");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once __DIR__ . '/../config/database.php';

$database = new Database();
$db = $database->getConnection();

if (!$db) {
    echo json_encode(["success" => false, "message" => "Error de conexión a MySQL", "data" => null]);
    exit();
}

try {
    // 1. Reporte 1: Venta del Día (Pedidos CERRADOS hoy)
    $ventasDiaStmt = $db->prepare("
        SELECT 
            COALESCE(SUM(total), 0) AS venta_total_hoy,
            COUNT(*) AS cantidad_pedidos_hoy
        FROM pedido 
        WHERE estado = 'CERRADO' AND DATE(fecha) = CURDATE()
    ");
    $ventasDiaStmt->execute();
    $ventasDia = $ventasDiaStmt->fetch(PDO::FETCH_ASSOC);
    $ventaTotalHoy = (float)($ventasDia['venta_total_hoy'] ?? 0.0);
    $pedidosHoy = (int)($ventasDia['cantidad_pedidos_hoy'] ?? 0);

    // 2. Reporte 2: Top 5 Platos Más Pedidos
    $topPlatosStmt = $db->prepare("
        SELECT 
            p.nombre AS nombre_plato,
            p.categoria,
            COALESCE(SUM(dp.cantidad), 0) AS total_vendido,
            COALESCE(SUM(dp.subtotal), 0) AS total_monto
        FROM detalle_pedido dp
        INNER JOIN pedido ped ON dp.id_pedido = ped.id
        INNER JOIN plato p ON dp.id_plato = p.id
        WHERE ped.estado = 'CERRADO'
        GROUP BY dp.id_plato, p.nombre, p.categoria
        ORDER BY total_vendido DESC, total_monto DESC
        LIMIT 5
    ");
    $topPlatosStmt->execute();
    $topPlatos = $topPlatosStmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($topPlatos as &$tp) {
        $tp['total_vendido'] = (int)$tp['total_vendido'];
        $tp['total_monto'] = (float)$tp['total_monto'];
    }

    // 3. Reporte 3: Venta por Mesa (Pedidos CERRADOS)
    $ventasMesaStmt = $db->prepare("
        SELECT 
            m.numero AS numero_mesa,
            COUNT(ped.id) AS total_pedidos,
            COALESCE(SUM(ped.total), 0) AS total_mesa
        FROM pedido ped
        INNER JOIN mesa m ON ped.id_mesa = m.id
        WHERE ped.estado = 'CERRADO'
        GROUP BY ped.id_mesa, m.numero
        ORDER BY m.numero ASC
    ");
    $ventasMesaStmt->execute();
    $ventasMesa = $ventasMesaStmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($ventasMesa as &$vm) {
        $vm['numero_mesa'] = (int)$vm['numero_mesa'];
        $vm['total_pedidos'] = (int)$vm['total_pedidos'];
        $vm['total_mesa'] = (float)$vm['total_mesa'];
    }

    echo json_encode([
        "success" => true,
        "message" => "Reportes generados correctamente",
        "data" => [
            "venta_total_hoy" => $ventaTotalHoy,
            "cantidad_pedidos_hoy" => $pedidosHoy,
            "tiene_ventas_hoy" => ($ventaTotalHoy > 0 || $pedidosHoy > 0),
            "top_platos" => $topPlatos,
            "ventas_por_mesa" => $ventasMesa
        ]
    ]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error al generar reportes: " . $e->getMessage(), "data" => null]);
}
?>
