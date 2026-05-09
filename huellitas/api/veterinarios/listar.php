<?php

/**
 * API Endpoint: Listar veterinarios
 * Método: GET
 * URL: /api/veterinarios/listar.php
 */

declare(strict_types=1);

header('Content-Type: application/json; charset=UTF-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

require_once __DIR__ . '/../../helpers/Response.php';
require_once __DIR__ . '/../../models/Usuario.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    Response::error('Método no permitido. Use GET.', 405);
}

try {
    $usuario = new Usuario();
    $veterinarios = $usuario->listarVeterinarios();
    Response::success($veterinarios, 'Veterinarios obtenidos correctamente.');
} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
