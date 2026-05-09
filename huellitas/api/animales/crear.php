<?php

/**
 * API Endpoint: Crear un nuevo animal
 * Método: POST
 * URL: /api/animales/crear.php
 * 
 * Body (JSON):
 * {
 *   "nombre": "Max",                (opcional)
 *   "id_tipo_animal": 1,            (obligatorio: 1=Perro, 2=Gato, 3=Otro)
 *   "raza": "Labrador",             (opcional)
 *   "descripcion": "Amigable...",   (opcional)
 *   "ubicacion": "Parque Central",  (obligatorio)
 *   "contacto": "3001234567",       (obligatorio)
 *   "imagen_url": "https://..."     (opcional)
 * }
 */

declare(strict_types=1);

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Manejar preflight request de CORS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once __DIR__ . '/../../helpers/Response.php';
require_once __DIR__ . '/../../models/Animal.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Método no permitido. Use POST.', 405);
}

// Leer el body JSON
$input = json_decode(file_get_contents('php://input'), true);

if ($input === null) {
    Response::error('El cuerpo de la solicitud debe ser JSON válido.');
}

// Validar campos obligatorios
$camposObligatorios = ['id_tipo_animal', 'ubicacion', 'contacto'];
$errores = [];

foreach ($camposObligatorios as $campo) {
    if (!isset($input[$campo]) || (is_string($input[$campo]) && trim($input[$campo]) === '')) {
        $errores[] = "El campo '{$campo}' es obligatorio.";
    }
}

if (!empty($errores)) {
    Response::error(implode(' ', $errores));
}

// Validar que id_tipo_animal sea numérico y válido (1-3)
$idTipo = (int) $input['id_tipo_animal'];
if ($idTipo < 1 || $idTipo > 3) {
    Response::error('El tipo de animal debe ser 1 (Perro), 2 (Gato) o 3 (Otro).');
}

try {
    $animal = new Animal();
    $animal->nombre = isset($input['nombre']) ? trim($input['nombre']) : null;
    $animal->id_tipo_animal = $idTipo;
    $animal->raza = isset($input['raza']) ? trim($input['raza']) : null;
    $animal->descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : null;
    $animal->ubicacion = trim($input['ubicacion']);
    $animal->contacto = trim($input['contacto']);
    $animal->imagen_url = isset($input['imagen_url']) ? trim($input['imagen_url']) : null;

    $nuevoId = $animal->crear();
    $animalCreado = $animal->obtenerPorId($nuevoId);

    Response::success($animalCreado, 'Animal registrado correctamente', 201);
} catch (Exception $e) {
    Response::error('Error al registrar el animal', 500);
}
