<?php

/**
 * API Endpoint: Registrar veterinario
 * Método: POST
 * URL: /api/veterinarios/crear.php
 * Body: { "nombre": "...", "telefono": "...", "correo": "...", "especializacion": "...", "password": "..." }
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
require_once __DIR__ . '/../../models/Usuario.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Método no permitido. Use POST.', 405);
}

$input = json_decode(file_get_contents('php://input'), true);

if ($input === null) {
    Response::error('El cuerpo de la solicitud debe ser JSON válido.');
}

// Validar campos obligatorios
$camposObligatorios = ['nombre', 'telefono', 'correo', 'especializacion', 'password'];
$errores = [];

foreach ($camposObligatorios as $campo) {
    if (!isset($input[$campo]) || trim((string) $input[$campo]) === '') {
        $errores[] = "El campo '{$campo}' es obligatorio.";
    }
}

if (!empty($errores)) {
    Response::error(implode(' ', $errores));
}

$nombre         = trim($input['nombre']);
$telefono       = trim($input['telefono']);
$correo         = trim($input['correo']);
$especializacion = trim($input['especializacion']);
$password       = $input['password'];

if (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
    Response::error('El formato del correo no es válido.');
}

if (strlen($password) < 4) {
    Response::error('La contraseña debe tener al menos 4 caracteres.');
}

try {
    $usuario = new Usuario();

    if ($usuario->existeCorreo($correo)) {
        Response::error('El correo ya está registrado.', 409);
    }

    $usuario->nombre          = $nombre;
    $usuario->apellidos       = '';
    $usuario->correo          = $correo;
    $usuario->telefono        = $telefono;
    $usuario->especializacion = $especializacion;
    $usuario->password        = password_hash($password, PASSWORD_BCRYPT);
    $usuario->rol_id          = 2; // Veterinario

    $usuario->crear();

    $datos = $usuario->obtenerPorCorreo($correo);
    unset($datos['password']);

    Response::success($datos, 'Veterinario registrado exitosamente.', 201);

} catch (Exception $e) {
    Response::error('Error interno del servidor.', 500);
}
