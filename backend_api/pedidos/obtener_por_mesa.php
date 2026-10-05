<?php
// ==========================================================
// SaborApp - Backend REST API
// Obtener Pedido Activo por Mesa (HU-08 / HU-09)
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

$id_mesa = isset($_GET['id_mesa']) ? (int)$_GET['id_mesa'] : 0;

if ($id_mesa <= 0) {
    echo json_encode(["success" => false, "message" => "ID de mesa inválido", "data" => null]);
    exit();
}

try {
    // 1. Obtener pedido abierto
    $pedidoStmt = $db->prepare("SELECT id, id_mesa, fecha, estado, total FROM pedido WHERE id_mesa = :id_mesa AND estado = 'ABIERTO' ORDER BY id DESC LIMIT 1");
    $pedidoStmt->bindParam(':id_mesa', $id_mesa, PDO::PARAM_INT);
    $pedidoStmt->execute();
    $pedido = $pedidoStmt->fetch(PDO::FETCH_ASSOC);

    if (!$pedido) {
        echo json_encode([
            "success" => true,
            "message" => "La mesa no tiene pedido abierto",
            "data" => null
        ]);
        exit();
    }

    $id_pedido = (int)$pedido['id'];

    // 2. Obtener detalles del pedido con JOIN a plato
    $detallesStmt = $db->prepare("
        SELECT 
            dp.id,
            dp.id_pedido,
            dp.id_plato,
            p.nombre AS nombre_plato,
            p.categoria AS categoria_plato,
            dp.cantidad,
            dp.precio_unit,
            dp.subtotal
        FROM detalle_pedido dp
        INNER JOIN plato p ON dp.id_plato = p.id
        WHERE dp.id_pedido = :id_pedido
        ORDER BY dp.id ASC
    ");
    $detallesStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
    $detallesStmt->execute();
    $detalles = $detallesStmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($detalles as &$d) {
        $d['id'] = (int)$d['id'];
        $d['id_pedido'] = (int)$d['id_pedido'];
        $d['id_plato'] = (int)$d['id_plato'];
        $d['cantidad'] = (int)$d['cantidad'];
        $d['precio_unit'] = (float)$d['precio_unit'];
        $d['subtotal'] = (float)$d['subtotal'];
    }

    $pedido['id'] = (int)$pedido['id'];
    $pedido['id_mesa'] = (int)$pedido['id_mesa'];
    $pedido['total'] = (float)$pedido['total'];
    $pedido['detalles'] = $detalles;

    echo json_encode([
        "success" => true,
        "message" => "Pedido obtenido correctamente",
        "data" => $pedido
    ]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error al obtener pedido: " . $e->getMessage(), "data" => null]);
}
?>
