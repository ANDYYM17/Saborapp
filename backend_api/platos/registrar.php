<?php
// ==========================================================
// SaborApp - Backend REST API
// Registrar Plato (HU-05)
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

if (!is_array($data)) {
    // Intentar convertir encoding si vino en latin1 o cp1252
    $utf8_input = mb_convert_encoding($raw_input, 'UTF-8', 'auto');
    $data = json_decode($utf8_input, true);
}

$nombre = "";
$categoria = "";
$precio = null;
$disponible = 1;

if (is_array($data)) {
    $nombre = trim($data['nombre'] ?? '');
    $categoria = trim($data['categoria'] ?? '');
    if (isset($data['precio']) && $data['precio'] !== '') {
        $precio = (float)$data['precio'];
    }
    if (isset($data['disponible'])) {
        $disponible = (int)$data['disponible'];
    }
} else {
    $nombre = trim($_POST['nombre'] ?? '');
    $categoria = trim($_POST['categoria'] ?? '');
    if (isset($_POST['precio']) && $_POST['precio'] !== '') {
        $precio = (float)$_POST['precio'];
    }
    if (isset($_POST['disponible'])) {
        $disponible = (int)$_POST['disponible'];
    }
}

// CA1: Validación de campos vacíos
if (empty($nombre)) {
    echo json_encode([
        "success" => false,
        "message" => "El nombre del plato no puede estar vacío",
        "data" => null
    ]);
    exit();
}

if ($precio === null) {
    echo json_encode([
        "success" => false,
        "message" => "El precio no puede estar vacío",
        "data" => null
    ]);
    exit();
}

// CA2: Validación de precio > 0
if ($precio <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Precio inválido",
        "data" => null
    ]);
    exit();
}

if (empty($categoria)) {
    $categoria = "Fondos";
}

try {
    $query = "INSERT INTO plato (nombre, categoria, precio, disponible) VALUES (:nombre, :categoria, :precio, :disponible)";
    $stmt = $db->prepare($query);
    $stmt->bindParam(':nombre', $nombre);
    $stmt->bindParam(':categoria', $categoria);
    $stmt->bindParam(':precio', $precio);
    $stmt->bindParam(':disponible', $disponible, PDO::PARAM_INT);
    $stmt->execute();

    $newId = (int)$db->lastInsertId();

    echo json_encode([
        "success" => true,
        "message" => "Plato registrado exitosamente",
        "data" => [
            "id" => $newId,
            "nombre" => $nombre,
            "categoria" => $categoria,
            "precio" => $precio,
            "disponible" => $disponible
        ]
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error al registrar plato: " . $e->getMessage(),
        "data" => null
    ]);
}
?>
