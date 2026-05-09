<?php

declare(strict_types=1);

require_once __DIR__ . '/../config/Database.php';

/**
 * Clase TipoAnimal - Modelo para la tabla tipos_animal.
 * Encapsula todas las operaciones CRUD del catálogo de tipos.
 */
class TipoAnimal
{
    private PDO $db;
    private string $table = 'tipos_animal';

    public function __construct()
    {
        $this->db = Database::getInstance()->getConnection();
    }

    /**
     * Obtener todos los tipos de animal.
     * 
     * @return array Lista de tipos de animal.
     */
    public function obtenerTodos(): array
    {
        $query = "SELECT id, nombre FROM {$this->table} ORDER BY id ASC";
        $stmt = $this->db->query($query);

        return $stmt->fetchAll();
    }

    /**
     * Obtener un tipo de animal por su ID.
     * 
     * @param int $id ID del tipo.
     * @return array|false Datos del tipo o false si no existe.
     */
    public function obtenerPorId(int $id): array|false
    {
        $query = "SELECT id, nombre FROM {$this->table} WHERE id = :id";
        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id', $id, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetch();
    }
}
