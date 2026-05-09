<?php

declare(strict_types=1);

require_once __DIR__ . '/../config/Database.php';

/**
 * Clase EstadoAnimal - Modelo para la tabla estados_animal.
 * Encapsula las operaciones de lectura del catálogo de estados.
 */
class EstadoAnimal
{
    private PDO $db;
    private string $table = 'estados_animal';

    public function __construct()
    {
        $this->db = Database::getInstance()->getConnection();
    }

    /**
     * Obtener todos los estados de animal.
     * 
     * @return array Lista de estados.
     */
    public function obtenerTodos(): array
    {
        $query = "SELECT id, nombre, descripcion FROM {$this->table} ORDER BY id ASC";
        $stmt = $this->db->query($query);

        return $stmt->fetchAll();
    }

    /**
     * Obtener un estado por su ID.
     * 
     * @param int $id ID del estado.
     * @return array|false Datos del estado o false si no existe.
     */
    public function obtenerPorId(int $id): array|false
    {
        $query = "SELECT id, nombre, descripcion FROM {$this->table} WHERE id = :id";
        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id', $id, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetch();
    }
}
