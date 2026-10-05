<?php
// ==========================================================
// SaborApp - Backend REST API
// Registrar Mesa (HU-06)
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
    echo json_encode([
        "success" => false,
        "message" => "Error de conexión a la base de datos MySQL",
        "data" => null
    ]);
    exit();
}

// Leer datos JSON o form-urlencoded
$raw_input = file_get_contents("php://input");
$data = json_decode($raw_input, true);

if (is_array($data)) {
    $numero = isset($data['numero']) ? (int)$data['numero'] : 0;
    $capacidad = isset($data['capacidad']) ? (int)$data['capacidad'] : 0;
} else {
    $numero = isset($_POST['numero']) ? (int)$_POST['numero'] : 0;
    $capacidad = isset($_POST['capacidad']) ? (int)$_POST['capacidad'] : 0;
}

if ($numero <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "El número de mesa debe ser mayor que 0",
        "data" => null
    ]);
    exit();
}

// CA2: Capacidad válida entre 1 y 12
if ($capacidad < 1 || $capacidad > 12) {
    echo json_encode([
        "success" => false,
        "message" => "Capacidad inválida",
        "data" => null
    ]);
    exit();
}

try {
    // CA1: Validar si la mesa ya existe
    $checkStmt = $db->prepare("SELECT id FROM mesa WHERE numero = :numero LIMIT 1");
    $checkStmt->bindParam(':numero', $numero, PDO::PARAM_INT);
    $checkStmt->execute();

    if ($checkStmt->fetch()) {
        echo json_encode([
            "success" => false,
            "message" => "La mesa ya existe",
            "data" => null
        ]);
        exit();
    }

    // CA3: Registrar mesa nueva con estado LIBRE
    $estado = "LIBRE";
    $query = "INSERT INTO mesa (numero, capacidad, estado) VALUES (:numero, :capacidad, :estado)";
    $stmt = $db->prepare($query);
    $stmt->bindParam(':numero', $numero, PDO::PARAM_INT);
    $stmt->bindParam(':capacidad', $capacidad, PDO::PARAM_INT);
    $stmt->bindParam(':estado', $estado);
    $stmt->execute();

    $newId = (int)$db->lastInsertId();

    echo json_encode([
        "success" => true,
        "message" => "Mesa registrada exitosamente",
        "data" => [
            "id" => $newId,
            "numero" => $numero,
            "capacidad" => $capacidad,
            "estado" => $estado
        ]
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error al registrar mesa: " . $e->getMessage(),
        "data" => null
    ]);
}
?>
