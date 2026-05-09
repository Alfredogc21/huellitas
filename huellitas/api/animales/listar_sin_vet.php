<?php

/**
 * API Endpoint: Animales activos sin veterinario asignado
 * Método: GET
 * URL: /api/animales/listar_sin_vet.php
 * Retorna animales con id_estado=1 (Activo) y id_veterinario IS NULL
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

try {
    $animal = new Animal();
    $animales = $animal->obtenerActivosSinVet();
    Response::success($animales, 'Animales disponibles obtenidos correctamente.');
} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
