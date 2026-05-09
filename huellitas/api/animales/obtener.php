<?php

/**
 * API Endpoint: Obtener un animal por ID
 * Método: GET
 * URL: /api/animales/obtener.php?id={id}
 */

declare(strict_types=1);

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

require_once __DIR__ . '/../../helpers/Response.php';
require_once __DIR__ . '/../../models/Animal.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    Response::error('Método no permitido. Use GET.', 405);
}

if (!isset($_GET['id']) || !is_numeric($_GET['id'])) {
    Response::error('El parámetro "id" es obligatorio y debe ser numérico.');
}

try {
    $animal = new Animal();
    $resultado = $animal->obtenerPorId((int) $_GET['id']);

    if (!$resultado) {
        Response::error('Animal no encontrado.', 404);
    }

    Response::success($resultado, 'Animal obtenido correctamente');
} catch (Exception $e) {
    Response::error('Error al obtener el animal', 500);
}
