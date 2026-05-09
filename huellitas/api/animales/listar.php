<?php

/**
 * API Endpoint: Listar todos los animales
 * Método: GET
 * URL: /api/animales/listar.php
 *
 * Parámetros opcionales (query string):
 *   - orden: 'fecha_registro' | 'nombre' | 'id' (default: fecha_registro)
 *   - direccion: 'ASC' | 'DESC' (default: DESC)
 *   - tipo: ID del tipo de animal para filtrar
 *   - pagina: Número de página (default: 1)
 *   - limite: Cantidad de resultados por página (default: 10)
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

    $limite = isset($_GET['limite']) ? max(1, (int) $_GET['limite']) : 10;
    $pagina = isset($_GET['pagina']) ? max(1, (int) $_GET['pagina']) : 1;
    $desplazamiento = ($pagina - 1) * $limite;

    $idTipo   = isset($_GET['tipo'])   ? (int) $_GET['tipo']   : null;
    $idEstado = isset($_GET['estado']) ? (int) $_GET['estado'] : null;

    if ($idTipo !== null && $idTipo > 0) {
        $animales = $animal->obtenerPorTipo($idTipo, $limite, $desplazamiento, $idEstado);
    } else {
        $orden     = $_GET['orden']     ?? 'fecha_registro';
        $direccion = $_GET['direccion'] ?? 'DESC';
        $animales  = $animal->obtenerTodos($orden, $direccion, $limite, $desplazamiento, $idEstado);
    }

    Response::success($animales, 'Animales obtenidos correctamente');
} catch (Exception $e) {
    Response::error('Error al obtener los animales', 500);
}