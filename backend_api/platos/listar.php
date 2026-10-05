<?php
// ==========================================================
// SaborApp - Backend REST API
// Listar Platos (HU-05)
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
    // CA3: Ordenado por categoría y luego por nombre
    $query = "SELECT id, nombre, categoria, precio, disponible FROM plato ORDER BY categoria ASC, nombre ASC";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $platos = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Formatear tipos
    foreach ($platos as &$p) {
        $p['id'] = (int)$p['id'];
        $p['precio'] = (float)$p['precio'];
        $p['disponible'] = (int)$p['disponible'];
    }

    echo json_encode([
        "success" => true,
        "message" => "Platos obtenidos correctamente",
        "data" => $platos
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error al consultar platos: " . $e->getMessage(),
        "data" => []
    ]);
}
?>
