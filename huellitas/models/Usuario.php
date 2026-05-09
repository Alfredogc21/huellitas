<?php

declare(strict_types=1);

require_once __DIR__ . '/../config/Database.php';

/**
 * Clase Usuario - Modelo para la tabla usuarios.
 * Encapsula operaciones de autenticación y registro.
 */
class Usuario
{
    private PDO $db;
    private string $table = 'usuarios';

    public ?int $id = null;
    public string $nombre;
    public string $apellidos = '';
    public string $correo;
    public string $password;
    public ?string $telefono = null;
    public ?string $especializacion = null;
    public int $rol_id = 1;

    public function __construct()
    {
        $this->db = Database::getInstance()->getConnection();
    }

    /**
     * Buscar usuario por correo electrónico.
     */
    public function obtenerPorCorreo(string $correo): array|false
    {
        $query = "SELECT u.id, u.nombre, u.apellidos, u.correo, u.telefono, u.especializacion, u.password,
                         u.rol_id, r.nombre AS rol, u.created_at, u.updated_at
                  FROM {$this->table} u
                  INNER JOIN roles r ON u.rol_id = r.id
                  WHERE u.correo = :correo
                  LIMIT 1";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':correo', $correo, PDO::PARAM_STR);
        $stmt->execute();

        return $stmt->fetch();
    }

    /**
     * Listar todos los veterinarios (rol_id = 2).
     */
    public function listarVeterinarios(): array
    {
        $query = "SELECT u.id, u.nombre, u.apellidos, u.correo, u.telefono, u.especializacion,
                         u.rol_id, r.nombre AS rol, u.created_at, u.updated_at
                  FROM {$this->table} u
                  INNER JOIN roles r ON u.rol_id = r.id
                  WHERE u.rol_id = 2
                  ORDER BY u.created_at DESC";

        $stmt = $this->db->prepare($query);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    /**
     * Registrar un nuevo usuario.
     */
    public function crear(): int
    {
        $query = "INSERT INTO {$this->table} (nombre, apellidos, correo, telefono, especializacion, password, rol_id)
                  VALUES (:nombre, :apellidos, :correo, :telefono, :especializacion, :password, :rol_id)";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':nombre', $this->nombre, PDO::PARAM_STR);
        $stmt->bindValue(':apellidos', $this->apellidos, PDO::PARAM_STR);
        $stmt->bindValue(':correo', $this->correo, PDO::PARAM_STR);
        $stmt->bindValue(':telefono', $this->telefono);
        $stmt->bindValue(':especializacion', $this->especializacion);
        $stmt->bindValue(':password', $this->password, PDO::PARAM_STR);
        $stmt->bindValue(':rol_id', $this->rol_id, PDO::PARAM_INT);
        $stmt->execute();

        return (int) $this->db->lastInsertId();
    }

    /**
     * Verificar si un correo ya está registrado.
     */
    public function existeCorreo(string $correo): bool
    {
        $query = "SELECT COUNT(*) FROM {$this->table} WHERE correo = :correo";
        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':correo', $correo, PDO::PARAM_STR);
        $stmt->execute();

        return (int) $stmt->fetchColumn() > 0;
    }
}
