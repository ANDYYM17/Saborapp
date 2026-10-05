<?php
// ==========================================================
// SaborApp - Backend REST API
// Listar Mesas (HU-06)
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
    echo json_encode([
        "success" => false,
        "message" => "Error de conexión a la base de datos MySQL",
        "data" => []
    ]);
    exit();
}

try {
    $query = "SELECT id, numero, capacidad, estado FROM mesa ORDER BY numero ASC";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $mesas = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($mesas as &$m) {
        $m['id'] = (int)$m['id'];
        $m['numero'] = (int)$m['numero'];
        $m['capacidad'] = (int)$m['capacidad'];
    }

    echo json_encode([
        "success" => true,
        "message" => "Mesas obtenidas correctamente",
        "data" => $mesas
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error al consultar mesas: " . $e->getMessage(),
        "data" => []
    ]);
}
?>

