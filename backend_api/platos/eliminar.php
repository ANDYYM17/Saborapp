<?php
// ==========================================================
// SaborApp - Backend REST API
// Eliminar Plato con Restricción de Integridad (HU-07)
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
    echo json_encode(["success" => false, "message" => "Error de conexión a MySQL"]);
    exit();
}

$raw_input = file_get_contents("php://input");
$data = json_decode($raw_input, true);
$id = isset($data['id']) ? (int)$data['id'] : (isset($_POST['id']) ? (int)$_POST['id'] : 0);

if ($id <= 0) {
    echo json_encode(["success" => false, "message" => "ID de plato inválido"]);
    exit();
}

try {
    // Restricción HU-07: Comprobar si el plato tiene pedidos asociados
    $checkStmt = $db->prepare("SELECT COUNT(*) as total FROM detalle_pedido WHERE id_plato = :id");
    $checkStmt->bindParam(':id', $id, PDO::PARAM_INT);
    $checkStmt->execute();
    $row = $checkStmt->fetch();

    if ($row && (int)$row['total'] > 0) {
        echo json_encode([
            "success" => false,
            "message" => "No se puede eliminar: tiene pedidos"
        ]);
        exit();
    }

    // Si no tiene pedidos, proceder a eliminar
    $deleteStmt = $db->prepare("DELETE FROM plato WHERE id = :id");
    $deleteStmt->bindParam(':id', $id, PDO::PARAM_INT);
    $deleteStmt->execute();

    echo json_encode([
        "success" => true,
        "message" => "Plato eliminado correctamente"
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error al eliminar plato: " . $e->getMessage()
    ]);
}
?>
