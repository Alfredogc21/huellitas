<?php

/**
 * API Endpoint: Asignar un veterinario a un animal existente
 * Método: POST
 * URL: /api/animales/asignar_vet.php
 * Body: { "id_animal": X, "id_veterinario": Y }
 * Efecto: cambia id_veterinario y pone id_estado=4 (En progreso)
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

$idAnimal       = isset($input['id_animal'])      ? (int) $input['id_animal']      : 0;
$idVeterinario  = isset($input['id_veterinario']) ? (int) $input['id_veterinario'] : 0;

if ($idAnimal <= 0)      Response::error('El campo "id_animal" es obligatorio.');
if ($idVeterinario <= 0) Response::error('El campo "id_veterinario" es obligatorio.');

try {
    $animal = new Animal();
    $ok     = $animal->asignarVeterinario($idAnimal, $idVeterinario);

    if (!$ok) {
        Response::error('No se pudo asignar el veterinario. Verifica que el animal exista.', 404);
    }

    $datos = $animal->obtenerPorId($idAnimal);
    Response::success($datos, 'Veterinario asignado correctamente. Animal en tratamiento.');
} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
