<?php

/**
 * API Endpoint: Animales asignados a un veterinario
 * Método: GET
 * URL: /api/animales/mis_animales_vet.php?id_veterinario=X
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

$idVeterinario = isset($_GET['id_veterinario']) ? (int) $_GET['id_veterinario'] : 0;

if ($idVeterinario <= 0) {
    Response::error('Se requiere el parámetro id_veterinario.');
}

try {
    $animal = new Animal();
    $animales = $animal->obtenerPorVeterinario($idVeterinario);
    Response::success($animales, 'Animales obtenidos correctamente.');
} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
