<?php

/**
 * API Endpoint: Actualizar estado de un animal
 * Método: POST
 * URL: /api/animales/actualizar_estado.php
 * Body: { "id": X, "id_estado": Y }
 * Estados: 1=Activo, 2=Adoptado, 3=Inactivo, 4=En progreso, 5=Rehabilitado, 6=Para adoptar
 */

declare(strict_types=1);

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once __DIR__ . '/../../helpers/Response.php';
require_once __DIR__ . '/../../models/Animal.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Método no permitido. Use POST.', 405);
}

$input = json_decode(file_get_contents('php://input'), true);

if ($input === null) {
    Response::error('El cuerpo de la solicitud debe ser JSON válido.');
}

$id       = isset($input['id'])       ? (int) $input['id']       : 0;
$idEstado = isset($input['id_estado']) ? (int) $input['id_estado'] : 0;
$fotoRehabilitacion = isset($input['foto_rehabilitacion']) ? trim($input['foto_rehabilitacion']) : null;
if ($fotoRehabilitacion === '') $fotoRehabilitacion = null;

if ($id <= 0) {
    Response::error('El campo "id" es obligatorio y debe ser un número válido.');
}

$estadosPermitidos = [1, 2, 3, 4, 5, 6];
if (!in_array($idEstado, $estadosPermitidos, true)) {
    Response::error('El campo "id_estado" debe ser 1, 2, 3, 4, 5 o 6.');
}

try {
    $animal = new Animal();

    $actualizado = $animal->actualizarEstado($id, $idEstado, $fotoRehabilitacion);

    if (!$actualizado) {
        Response::error('No se pudo actualizar el estado. Verifica que el animal exista.', 404);
    }

    $datos = $animal->obtenerPorId($id);
    Response::success($datos, 'Estado actualizado correctamente.');
} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
