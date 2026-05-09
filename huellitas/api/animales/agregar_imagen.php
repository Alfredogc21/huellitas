<?php

/**
 * API Endpoint: Agregar imagen a un animal
 * Método: POST
 * URL: /api/animales/agregar_imagen.php
 * 
 * Body (JSON):
 * {
 *   "id_animal": 1,                    (obligatorio)
 *   "imagen_url": "https://...",        (obligatorio)
 *   "es_principal": false               (opcional, default: false)
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

if (!isset($input['id_animal']) || !is_numeric($input['id_animal'])) {
    Response::error('El campo "id_animal" es obligatorio y debe ser numérico.');
}

if (!isset($input['imagen_url']) || trim($input['imagen_url']) === '') {
    Response::error('El campo "imagen_url" es obligatorio.');
}

try {
    $animal = new Animal();

    // Verificar que el animal existe
    $animalExistente = $animal->obtenerPorId((int) $input['id_animal']);
    if (!$animalExistente) {
        Response::error('Animal no encontrado.', 404);
    }

    $esPrincipal = isset($input['es_principal']) && $input['es_principal'] === true;

    $animal->agregarImagen(
        (int) $input['id_animal'],
        trim($input['imagen_url']),
        $esPrincipal
    );

    // Retornar el animal actualizado con sus imágenes
    $animalActualizado = $animal->obtenerPorId((int) $input['id_animal']);

    Response::success($animalActualizado, 'Imagen agregada correctamente', 201);
} catch (Exception $e) {
    Response::error('Error al agregar la imagen', 500);
}
