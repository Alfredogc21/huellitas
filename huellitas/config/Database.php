<?php

declare(strict_types=1);

/**
 * Clase Database - Conexión a la base de datos usando PDO (Singleton)
 * 
 * Lee las credenciales desde config/env.php para que solo
 * se cambie un archivo al migrar de XAMPP a HostGator.
 */
class Database
{
    private static ?Database $instance = null;
    private PDO $connection;

    /**
     * Constructor privado para evitar instanciación directa.
     * Configura PDO con manejo de errores por excepciones.
     */
    private function __construct()
    {
        $env = require __DIR__ . '/env.php';

        $dsn = sprintf(
            'mysql:host=%s;dbname=%s;charset=%s',
            $env['DB_HOST'],
            $env['DB_NAME'],
            $env['DB_CHARSET']
        );

        $options = [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
            PDO::ATTR_STRINGIFY_FETCHES  => false,
        ];

        try {
            $this->connection = new PDO($dsn, $env['DB_USER'], $env['DB_PASS'], $options);
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode([
                'status'  => false,
                'message' => 'Error de conexión a la base de datos',
            ]);
            exit;
        }
    }

    /** Evitar clonación del Singleton */
    private function __clone() {}

    /** Evitar deserialización del Singleton */
    public function __wakeup()
    {
        throw new \Exception('No se puede deserializar un Singleton.');
    }

    /**
     * Obtener la instancia única de Database.
     */
    public static function getInstance(): self
    {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    /**
     * Obtener el objeto PDO de conexión.
     */
    public function getConnection(): PDO
    {
        return $this->connection;
    }
}
