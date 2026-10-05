<?php
// ==========================================================
// SaborApp - Backend REST API
// Listar y Buscar Platos (HU-05 / HU-07)
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

$search = isset($_GET['search']) ? trim($_GET['search']) : '';
$soloDisponibles = isset($_GET['solo_disponibles']) && $_GET['solo_disponibles'] == '1';

try {
    $sql = "SELECT id, nombre, categoria, precio, disponible FROM plato WHERE 1=1";
    $params = [];

    if ($soloDisponibles) {
        $sql .= " AND disponible = 1";
    }

    if (!empty($search)) {
        $sql .= " AND (nombre LIKE :search OR categoria LIKE :search)";
        $params[':search'] = '%' . $search . '%';
    }

    $sql .= " ORDER BY categoria ASC, nombre ASC";

    $stmt = $db->prepare($sql);
    foreach ($params as $key => $val) {
        $stmt->bindValue($key, $val);
    }
    $stmt->execute();
    $platos = $stmt->fetchAll(PDO::FETCH_ASSOC);

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
