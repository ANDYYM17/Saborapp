<?php
// ==========================================================
// SaborApp - Backend REST API
// Estado del Servidor y Base de Datos
// ==========================================================

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

require_once __DIR__ . '/config/database.php';

$database = new Database();
$db = $database->getConnection();

if ($db) {
    echo json_encode([
        "status" => "online",
        "app" => "SaborApp - Pollería El Buen Sabor",
        "database" => "saborapp",
        "connected" => true,
        "timestamp" => date("Y-m-d H:i:s")
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "app" => "SaborApp - Pollería El Buen Sabor",
        "connected" => false,
        "message" => "No se pudo conectar a la base de datos MySQL"
    ]);
}
?>

