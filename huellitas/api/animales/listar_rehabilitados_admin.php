<?php

/**
 * API Endpoint: Listar animales rehabilitados y para adoptar (uso exclusivo del admin)
 * Método: GET
 * URL: /api/animales/listar_rehabilitados_admin.php
 *
 * Devuelve dos grupos:
 *   - rehabilitados: id_estado = 5 (vienen del vet, pendientes de aprobación admin)
 *   - para_adoptar:  id_estado = 6 (ya aprobados, visibles en el feed público)
 */

declare(strict_types=1);

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once __DIR__ . '/../../helpers/Response.php';
require_once __DIR__ . '/../../models/Animal.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    Response::error('Método no permitido. Use GET.', 405);
}

try {
    $animal = new Animal();

    // Perros rehabilitados (pendientes de aprobación admin)
    $rehabilitados = $animal->obtenerPorTipo(1, 50, 0, 5);

    // Perros ya aprobados para adopción pública
    $paraAdoptar = $animal->obtenerPorTipo(1, 50, 0, 6);

    Response::success(
        [
            'rehabilitados' => $rehabilitados,
            'para_adoptar'  => $paraAdoptar,
        ],
        'Animales obtenidos correctamente'
    );
} catch (Exception $e) {
    Response::error('Error al obtener animales.', 500);
}
