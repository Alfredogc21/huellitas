<?php

/**
 * API Endpoint: Subir imagen de un animal
 * Método: POST (multipart/form-data)
 * URL: /api/animales/subir_imagen.php
 * 
 * Campos (form-data):
 *   - imagen: archivo de imagen (jpg, jpeg, png, webp) máx 5MB
 *   - id_animal: (opcional) ID del animal si ya existe
 * 
 * Responde con la URL pública de la imagen subida.
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

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    Response::error('Método no permitido. Use POST.', 405);
}

// Validar que se envió un archivo
if (!isset($_FILES['imagen']) || $_FILES['imagen']['error'] !== UPLOAD_ERR_OK) {
    $errores = [
        UPLOAD_ERR_INI_SIZE   => 'La imagen excede el tamaño máximo del servidor.',
        UPLOAD_ERR_FORM_SIZE  => 'La imagen excede el tamaño máximo permitido.',
        UPLOAD_ERR_PARTIAL    => 'La imagen se subió parcialmente.',
        UPLOAD_ERR_NO_FILE    => 'No se envió ninguna imagen.',
        UPLOAD_ERR_NO_TMP_DIR => 'Error de configuración del servidor.',
        UPLOAD_ERR_CANT_WRITE => 'Error al escribir la imagen en disco.',
    ];
    $codigoError = $_FILES['imagen']['error'] ?? UPLOAD_ERR_NO_FILE;
    $mensaje = $errores[$codigoError] ?? 'Error desconocido al subir la imagen.';
    Response::error($mensaje);
}

$archivo = $_FILES['imagen'];

// ── Validaciones de seguridad ──

// 1. Validar tipo MIME real (no confiar en la extensión)
$tiposPermitidos = ['image/jpeg', 'image/png', 'image/webp'];
$tipoReal = mime_content_type($archivo['tmp_name']);

if (!in_array($tipoReal, $tiposPermitidos, true)) {
    Response::error('Formato no permitido. Use JPG, PNG o WEBP.');
}

// 2. Validar tamaño (máximo 5MB)
$maxBytes = 5 * 1024 * 1024;
if ($archivo['size'] > $maxBytes) {
    Response::error('La imagen no puede pesar más de 5MB.');
}

// 3. Validar que realmente es una imagen
$infoImagen = getimagesize($archivo['tmp_name']);
if ($infoImagen === false) {
    Response::error('El archivo no es una imagen válida.');
}

// ── Generar nombre único y mover archivo ──
$extensiones = [
    'image/jpeg' => 'jpg',
    'image/png'  => 'png',
    'image/webp' => 'webp',
];
$extension = $extensiones[$tipoReal];
$nombreArchivo = uniqid('animal_', true) . '.' . $extension;

// Determinar subcarpeta según el tipo de foto
$tipoFoto = isset($_POST['tipo']) ? trim($_POST['tipo']) : '';
$subcarpeta = match ($tipoFoto) {
    'rehabilitado' => 'rehabilitado/',
    'tratamiento'  => 'tratamientos/',
    default        => '',
};

// Crear carpeta destino si no existe
$carpetaBase    = __DIR__ . '/../../uploads/';
$carpetaDestino = $carpetaBase . $subcarpeta;
if (!is_dir($carpetaDestino)) {
    mkdir($carpetaDestino, 0755, true);
}

$rutaCompleta = $carpetaDestino . $nombreArchivo;

if (!move_uploaded_file($archivo['tmp_name'], $rutaCompleta)) {
    Response::error('Error al guardar la imagen en el servidor.', 500);
}

// ── Construir la URL pública ──
$protocolo = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
$host = $_SERVER['HTTP_HOST'];

// Detectar la ruta base dinámicamente
$scriptDir = dirname($_SERVER['SCRIPT_NAME']); // /huellitas/api/animales
$baseDir = dirname(dirname($scriptDir));        // /huellitas
$urlImagen = $protocolo . '://' . $host . $baseDir . '/uploads/' . $subcarpeta . $nombreArchivo;

// ── Si se envió id_animal, registrar en la BD ──
$idAnimal = isset($_POST['id_animal']) ? (int) $_POST['id_animal'] : null;

if ($idAnimal !== null && $idAnimal > 0) {
    require_once __DIR__ . '/../../models/Animal.php';
    try {
        $modeloAnimal = new Animal();
        $modeloAnimal->agregarImagen($idAnimal, $urlImagen, true);
    } catch (Exception $e) {
        // La imagen ya se subió, solo no se vinculó - no es error crítico
    }
}

Response::success(
    ['imagen_url' => $urlImagen],
    'Imagen subida correctamente',
    201
);
