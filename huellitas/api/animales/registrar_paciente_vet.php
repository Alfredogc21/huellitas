<?php

/**
 * API Endpoint: Registrar un nuevo animal recibido por el veterinario
 * Método: POST
 * URL: /api/animales/registrar_paciente_vet.php
 * Body: {
 *   "nombre": "Firulais",
 *   "id_tipo_animal": 1,
 *   "raza": "Mestizo",
 *   "descripcion": "Llegó herido",
 *   "ubicacion": "Clínica central",
 *   "contacto": "31111111",
 *   "id_veterinario": 3,
 *   "foto_ingreso": "https://..."  (opcional)
 * }
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

$idVeterinario = isset($input['id_veterinario']) ? (int) $input['id_veterinario'] : 0;
if ($idVeterinario <= 0) {
    Response::error('El campo "id_veterinario" es obligatorio.');
}

$ubicacion = isset($input['ubicacion']) ? trim($input['ubicacion']) : '';
$contacto  = isset($input['contacto'])  ? trim($input['contacto'])  : '';
if (empty($ubicacion)) Response::error('El campo "ubicacion" es obligatorio.');
if (empty($contacto))  Response::error('El campo "contacto" es obligatorio.');

try {
    $animal = new Animal();
    $animal->nombre         = isset($input['nombre']) ? trim($input['nombre']) : null;
    $animal->id_tipo_animal = isset($input['id_tipo_animal']) ? (int) $input['id_tipo_animal'] : 1;
    $animal->raza           = isset($input['raza']) ? trim($input['raza']) : null;
    $animal->descripcion    = isset($input['descripcion']) ? trim($input['descripcion']) : null;
    $animal->ubicacion      = $ubicacion;
    $animal->contacto       = $contacto;
    $animal->id_estado      = 4; // En progreso (tratamiento veterinario)
    $animal->id_veterinario = $idVeterinario;
    $animal->foto_ingreso   = isset($input['foto_ingreso']) ? trim($input['foto_ingreso']) : null;
    if ($animal->foto_ingreso === '') $animal->foto_ingreso = null;

    $idCreado = $animal->crear();
    $datos    = $animal->obtenerPorId($idCreado);

    Response::success($datos, 'Paciente registrado correctamente.');
} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
