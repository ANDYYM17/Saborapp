<?php
// ==========================================================
// SaborApp - Backend REST API
// Actualizar Plato (HU-07)
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
if (!is_array($data)) {
    $utf8_input = mb_convert_encoding($raw_input, 'UTF-8', 'auto');
    $data = json_decode($utf8_input, true);
}

$id = isset($data['id']) ? (int)$data['id'] : (isset($_POST['id']) ? (int)$_POST['id'] : 0);
$nombre = trim($data['nombre'] ?? $_POST['nombre'] ?? '');
$categoria = trim($data['categoria'] ?? $_POST['categoria'] ?? '');
$precio = isset($data['precio']) ? (float)$data['precio'] : (isset($_POST['precio']) ? (float)$_POST['precio'] : null);
$disponible = isset($data['disponible']) ? (int)$data['disponible'] : (isset($_POST['disponible']) ? (int)$_POST['disponible'] : 1);

if ($id <= 0) {
    echo json_encode(["success" => false, "message" => "ID de plato inválido", "data" => null]);
    exit();
}

if (empty($nombre)) {
    echo json_encode(["success" => false, "message" => "El nombre del plato no puede estar vacío", "data" => null]);
    exit();
}

if ($precio === null || $precio <= 0) {
    echo json_encode(["success" => false, "message" => "Precio inválido", "data" => null]);
    exit();
}

if (empty($categoria)) {
    $categoria = "Fondos";
}

try {
    $query = "UPDATE plato SET nombre = :nombre, categoria = :categoria, precio = :precio, disponible = :disponible WHERE id = :id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(':nombre', $nombre);
    $stmt->bindParam(':categoria', $categoria);
    $stmt->bindParam(':precio', $precio);
    $stmt->bindParam(':disponible', $disponible, PDO::PARAM_INT);
    $stmt->bindParam(':id', $id, PDO::PARAM_INT);
    $stmt->execute();

    echo json_encode([
        "success" => true,
        "message" => "Plato actualizado exitosamente",
        "data" => [
            "id" => $id,
            "nombre" => $nombre,
            "categoria" => $categoria,
            "precio" => $precio,
            "disponible" => $disponible
        ]
    ]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Error al actualizar plato: " . $e->getMessage(), "data" => null]);
}
?>
