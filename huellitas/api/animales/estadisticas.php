<?php

/**
 * API Endpoint: Estadísticas del panel administrativo
 * Método: GET
 * URL: /api/animales/estadisticas.php
 * Respuesta:
 * {
 *   "total":         N,   -- todos los animales registrados
 *   "activos":       N,   -- estado 1 (en la calle, activos)
 *   "en_progreso":   N,   -- estado 4 (en tratamiento veterinario)
 *   "rehabilitados": N,   -- estado 5 (rehabilitados, pendientes de aprobación)
 *   "para_adoptar":  N,   -- estado 6 (aprobados por admin, en feed)
 *   "adoptados":     N,   -- estado 2 (adoptados exitosamente)
 *   "inactivos":     N    -- estado 3 (registros desactivados)
 * }
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

require_once __DIR__ . '/../../config/Database.php';
require_once __DIR__ . '/../../helpers/Response.php';

try {
    $db = Database::getInstance()->getConnection();

    // Un solo query con GROUP BY para obtener conteo por estado
    $sql = "
        SELECT id_estado, COUNT(*) AS total
        FROM animales
        GROUP BY id_estado
    ";
    $stmt = $db->prepare($sql);
    $stmt->execute();
    $filas = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Inicializar conteos
    $conteos = [
        1 => 0, // Activo
        2 => 0, // Adoptado
        3 => 0, // Inactivo
        4 => 0, // En progreso
        5 => 0, // Rehabilitado
        6 => 0, // Para adoptar
    ];

    $totalGeneral = 0;
    foreach ($filas as $fila) {
        $id = (int) $fila['id_estado'];
        $n  = (int) $fila['total'];
        if (isset($conteos[$id])) {
            $conteos[$id] = $n;
        }
        $totalGeneral += $n;
    }

    $datos = [
        'total'         => $totalGeneral,
        'activos'       => $conteos[1],
        'adoptados'     => $conteos[2],
        'inactivos'     => $conteos[3],
        'en_progreso'   => $conteos[4],
        'rehabilitados' => $conteos[5],
        'para_adoptar'  => $conteos[6],
    ];

    Response::success($datos, 'Estadísticas obtenidas correctamente.');
} catch (Exception $e) {
    Response::error('Error al obtener estadísticas.', 500);
}
