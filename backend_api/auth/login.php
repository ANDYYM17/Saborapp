<?php
// ==========================================================
// SaborApp - Backend REST API
// Autenticación de Usuario (HU-01)
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

// Leer datos enviados en formato JSON o POST form-urlencoded
$raw_input = file_get_contents("php://input");
$data = json_decode($raw_input, true);

$usuario = "";
$clave = "";

if (is_array($data)) {
    $usuario = trim($data['usuario'] ?? '');
    $clave = trim($data['clave'] ?? '');
} else {
    $usuario = trim($_POST['usuario'] ?? '');
    $clave = trim($_POST['clave'] ?? '');
}

if (empty($usuario) || empty($clave)) {
    echo json_encode([
        "success" => false,
        "message" => "Debe ingresar usuario y contraseña",
        "data" => null
    ]);
    exit();
}

try {
    $query = "SELECT id, usuario, clave, rol FROM usuario WHERE usuario = :usuario LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(':usuario', $usuario);
    $stmt->execute();

    if ($row = $stmt->fetch()) {
        // En Sprint 1 verificamos clave en texto plano o hash (preparado para hash futuro)
        if ($clave === $row['clave'] || password_verify($clave, $row['clave'])) {
            echo json_encode([
                "success" => true,
                "message" => "Inicio de sesión exitoso",
                "data" => [
                    "id" => (int)$row['id'],
                    "usuario" => $row['usuario'],
                    "rol" => $row['rol']
                ]
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Credenciales incorrectas",
                "data" => null
            ]);
        }
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Credenciales incorrectas",
            "data" => null
        ]);
    }
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error interno: " . $e->getMessage(),
        "data" => null
    ]);
}
?>
