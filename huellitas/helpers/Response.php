<?php

declare(strict_types=1);

/**
 * Clase Response - Utilidad para estandarizar las respuestas JSON de la API.
 */
class Response
{
    /**
     * Enviar respuesta JSON exitosa.
     */
    public static function success(mixed $data, string $message = 'Operación exitosa', int $code = 200): void
    {
        http_response_code($code);
        echo json_encode([
            'status'  => true,
            'message' => $message,
            'data'    => $data,
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }

    /**
     * Enviar respuesta JSON de error.
     */
    public static function error(string $message, int $code = 400): void
    {
        http_response_code($code);
        echo json_encode([
            'status'  => false,
            'message' => $message,
            'data'    => null,
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}
