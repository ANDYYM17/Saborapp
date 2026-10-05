<?php
// ==========================================================
// SaborApp - Backend REST API
// Cerrar Cuenta con Transacción (HU-09)
// ==========================================================

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

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

$raw_input = file_get_contents("php://input");
$data = json_decode($raw_input, true);
$id_mesa = isset($data['id_mesa']) ? (int)$data['id_mesa'] : (isset($_POST['id_mesa']) ? (int)$_POST['id_mesa'] : 0);

if ($id_mesa <= 0) {
    echo json_encode(["success" => false, "message" => "ID de mesa inválido", "data" => null]);
    exit();
}

try {
    // INICIAR TRANSACCIÓN (HU-09)
    $db->beginTransaction();

    // 1. Obtener pedido ABIERTO de la mesa
    $pedidoStmt = $db->prepare("SELECT id FROM pedido WHERE id_mesa = :id_mesa AND estado = 'ABIERTO' ORDER BY id DESC LIMIT 1 FOR UPDATE");
    $pedidoStmt->bindParam(':id_mesa', $id_mesa, PDO::PARAM_INT);
    $pedidoStmt->execute();
    $pedido = $pedidoStmt->fetch(PDO::FETCH_ASSOC);

    if (!$pedido) {
        $db->rollBack();
        echo json_encode(["success" => false, "message" => "La mesa no tiene ninguna cuenta abierta para cerrar", "data" => null]);
        exit();
    }

    $id_pedido = (int)$pedido['id'];

    // 2. Calcular total final de los subtotales
    $totalStmt = $db->prepare("SELECT COALESCE(SUM(subtotal), 0) AS total_final FROM detalle_pedido WHERE id_pedido = :id_pedido");
    $totalStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
    $totalStmt->execute();
    $totalFinal = (float)$totalStmt->fetchColumn();

    // 3. Actualizar pedido a CERRADO y guardar total
    $updPedStmt = $db->prepare("UPDATE pedido SET estado = 'CERRADO', total = :total WHERE id = :id_pedido");
    $updPedStmt->bindParam(':total', $totalFinal);
    $updPedStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
    $updPedStmt->execute();

    // 4. Actualizar mesa a LIBRE
    $updMesaStmt = $db->prepare("UPDATE mesa SET estado = 'LIBRE' WHERE id = :id_mesa");
    $updMesaStmt->bindParam(':id_mesa', $id_mesa, PDO::PARAM_INT);
    $updMesaStmt->execute();

    // CONFIRMAR TRANSACCIÓN
    $db->commit();

    echo json_encode([
        "success" => true,
        "message" => "Cuenta cerrada exitosamente. La mesa ahora está LIBRE.",
        "data" => [
            "id_pedido" => $id_pedido,
            "id_mesa" => $id_mesa,
            "total" => $totalFinal,
            "estado_mesa" => "LIBRE",
            "estado_pedido" => "CERRADO"
        ]
    ]);
} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    echo json_encode([
        "success" => false,
        "message" => "Error al cerrar la cuenta: " . $e->getMessage(),
        "data" => null
    ]);
}
?>
