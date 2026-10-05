<?php
// ==========================================================
// SaborApp - Backend REST API
// Agregar Item a Pedido con Transacción (HU-08)
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

$id_mesa = isset($data['id_mesa']) ? (int)$data['id_mesa'] : (isset($_POST['id_mesa']) ? (int)$_POST['id_mesa'] : 0);
$id_plato = isset($data['id_plato']) ? (int)$data['id_plato'] : (isset($_POST['id_plato']) ? (int)$_POST['id_plato'] : 0);
$cantidad = isset($data['cantidad']) ? (int)$data['cantidad'] : (isset($_POST['cantidad']) ? (int)$_POST['cantidad'] : 1);

if ($id_mesa <= 0 || $id_plato <= 0) {
    echo json_encode(["success" => false, "message" => "Datos de mesa o plato inválidos", "data" => null]);
    exit();
}

if ($cantidad <= 0) {
    echo json_encode(["success" => false, "message" => "La cantidad debe ser mayor que 0", "data" => null]);
    exit();
}

try {
    // INICIAR TRANSACCIÓN (HU-08)
    $db->beginTransaction();

    // 1. Obtener plato y verificar disponibilidad
    $platoStmt = $db->prepare("SELECT id, nombre, precio, disponible FROM plato WHERE id = :id_plato LIMIT 1");
    $platoStmt->bindParam(':id_plato', $id_plato, PDO::PARAM_INT);
    $platoStmt->execute();
    $plato = $platoStmt->fetch(PDO::FETCH_ASSOC);

    if (!$plato) {
        $db->rollBack();
        echo json_encode(["success" => false, "message" => "Plato no encontrado", "data" => null]);
        exit();
    }

    if ((int)$plato['disponible'] != 1) {
        $db->rollBack();
        echo json_encode(["success" => false, "message" => "El plato no se encuentra disponible", "data" => null]);
        exit();
    }

    $precio_unit = (float)$plato['precio'];

    // 2. Verificar o crear pedido ABIERTO para la mesa
    $pedidoStmt = $db->prepare("SELECT id FROM pedido WHERE id_mesa = :id_mesa AND estado = 'ABIERTO' ORDER BY id DESC LIMIT 1 FOR UPDATE");
    $pedidoStmt->bindParam(':id_mesa', $id_mesa, PDO::PARAM_INT);
    $pedidoStmt->execute();
    $pedido = $pedidoStmt->fetch(PDO::FETCH_ASSOC);

    $id_pedido = 0;
    if ($pedido) {
        $id_pedido = (int)$pedido['id'];
    } else {
        // Crear nuevo pedido
        $nuevoPedidoStmt = $db->prepare("INSERT INTO pedido (id_mesa, estado, total) VALUES (:id_mesa, 'ABIERTO', 0.00)");
        $nuevoPedidoStmt->bindParam(':id_mesa', $id_mesa, PDO::PARAM_INT);
        $nuevoPedidoStmt->execute();
        $id_pedido = (int)$db->lastInsertId();

        // Actualizar mesa a OCUPADA
        $updMesaStmt = $db->prepare("UPDATE mesa SET estado = 'OCUPADA' WHERE id = :id_mesa");
        $updMesaStmt->bindParam(':id_mesa', $id_mesa, PDO::PARAM_INT);
        $updMesaStmt->execute();
    }

    // 3. Verificar si el plato ya está en detalle_pedido de este pedido
    $detCheckStmt = $db->prepare("SELECT id, cantidad FROM detalle_pedido WHERE id_pedido = :id_pedido AND id_plato = :id_plato LIMIT 1 FOR UPDATE");
    $detCheckStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
    $detCheckStmt->bindParam(':id_plato', $id_plato, PDO::PARAM_INT);
    $detCheckStmt->execute();
    $existente = $detCheckStmt->fetch(PDO::FETCH_ASSOC);

    if ($existente) {
        $nuevaCantidad = (int)$existente['cantidad'] + $cantidad;
        $nuevoSubtotal = $nuevaCantidad * $precio_unit;

        $updDetStmt = $db->prepare("UPDATE detalle_pedido SET cantidad = :cantidad, precio_unit = :precio_unit, subtotal = :subtotal WHERE id = :id");
        $updDetStmt->bindParam(':cantidad', $nuevaCantidad, PDO::PARAM_INT);
        $updDetStmt->bindParam(':precio_unit', $precio_unit);
        $updDetStmt->bindParam(':subtotal', $nuevoSubtotal);
        $updDetStmt->bindParam(':id', $existente['id'], PDO::PARAM_INT);
        $updDetStmt->execute();
    } else {
        $subtotal = $cantidad * $precio_unit;
        $insDetStmt = $db->prepare("INSERT INTO detalle_pedido (id_pedido, id_plato, cantidad, precio_unit, subtotal) VALUES (:id_pedido, :id_plato, :cantidad, :precio_unit, :subtotal)");
        $insDetStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
        $insDetStmt->bindParam(':id_plato', $id_plato, PDO::PARAM_INT);
        $insDetStmt->bindParam(':cantidad', $cantidad, PDO::PARAM_INT);
        $insDetStmt->bindParam(':precio_unit', $precio_unit);
        $insDetStmt->bindParam(':subtotal', $subtotal);
        $insDetStmt->execute();
    }

    // 4. Recalcular total del pedido
    $totalStmt = $db->prepare("SELECT SUM(subtotal) AS nuevo_total FROM detalle_pedido WHERE id_pedido = :id_pedido");
    $totalStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
    $totalStmt->execute();
    $nuevoTotal = (float)$totalStmt->fetchColumn();

    $updTotalStmt = $db->prepare("UPDATE pedido SET total = :total WHERE id = :id_pedido");
    $updTotalStmt->bindParam(':total', $nuevoTotal);
    $updTotalStmt->bindParam(':id_pedido', $id_pedido, PDO::PARAM_INT);
    $updTotalStmt->execute();

    // CONFIRMAR TRANSACCIÓN
    $db->commit();

    echo json_encode([
        "success" => true,
        "message" => "Item agregado al pedido exitosamente",
        "data" => [
            "id_pedido" => $id_pedido,
            "id_mesa" => $id_mesa,
            "total" => $nuevoTotal
        ]
    ]);
} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    echo json_encode([
        "success" => false,
        "message" => "Error al procesar el pedido: " . $e->getMessage(),
        "data" => null
    ]);
}
?>
