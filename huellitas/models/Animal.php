<?php

declare(strict_types=1);

require_once __DIR__ . '/../config/Database.php';

/**
 * Clase Animal - Modelo para la tabla animales.
 * Encapsula todas las operaciones CRUD de animales.
 */
class Animal
{
    private PDO $db;
    private string $table = 'animales';

    // Propiedades públicas para binding de datos
    public ?int $id = null;
    public ?string $nombre = null;
    public int $id_tipo_animal;
    public ?string $raza = null;
    public ?string $descripcion = null;
    public string $ubicacion;
    public string $contacto;
    public int $id_estado = 1;
    public ?int $id_veterinario = null;
    public ?string $foto_ingreso = null;
    public ?string $foto_rehabilitacion = null;
    public ?string $imagen_url = null;

    public function __construct()
    {
        $this->db = Database::getInstance()->getConnection();
    }

    /**
     * Obtener todos los animales con datos de tipo y estado (JOIN).
     * Incluye la imagen principal si existe.
     * 
     * @param string $orden Columna para ordenar ('fecha_registro').
     * @param string $direccion Dirección del orden ('DESC' o 'ASC').
     * @return array Lista de animales.
     */
    public function obtenerTodos(string $orden = 'fecha_registro', string $direccion = 'DESC', int $limite = 10, int $desplazamiento = 0, ?int $idEstado = null): array
    {
        // Validar columnas permitidas para evitar SQL injection
        $columnasPermitidas = ['fecha_registro', 'nombre', 'id'];
        $direccionesPermitidas = ['ASC', 'DESC'];

        $orden = in_array($orden, $columnasPermitidas, true) ? $orden : 'fecha_registro';
        $direccion = in_array(strtoupper($direccion), $direccionesPermitidas, true) ? strtoupper($direccion) : 'DESC';

        $whereEstado = ($idEstado !== null && $idEstado > 0) ? 'WHERE a.id_estado = :id_estado' : '';

        $query = "SELECT 
                    a.id,
                    a.nombre,
                    ta.id AS id_tipo_animal,
                    ta.nombre AS tipo_animal,
                    a.raza,
                    a.descripcion,
                    a.ubicacion,
                    a.contacto,
                    ea.id AS id_estado,
                    ea.nombre AS estado,
                    a.fecha_registro,
                    a.updated_at,
                    COALESCE(a.foto_rehabilitacion, a.foto_ingreso, img.imagen_url) AS imagen_url
                  FROM {$this->table} a
                  INNER JOIN tipos_animal ta ON a.id_tipo_animal = ta.id
                  INNER JOIN estados_animal ea ON a.id_estado = ea.id
                  LEFT JOIN imagenes_animal img ON img.id_animal = a.id AND img.es_principal = 1
                  {$whereEstado}
                  ORDER BY a.{$orden} {$direccion}
                  LIMIT :limite OFFSET :desplazamiento";

        $stmt = $this->db->prepare($query);
        if ($idEstado !== null && $idEstado > 0) {
            $stmt->bindValue(':id_estado', $idEstado, PDO::PARAM_INT);
        }
        $stmt->bindValue(':limite', $limite, PDO::PARAM_INT);
        $stmt->bindValue(':desplazamiento', $desplazamiento, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    /**
     * Obtener un animal por su ID con datos completos.
     * 
     * @param int $id ID del animal.
     * @return array|false Datos del animal o false si no existe.
     */
    public function obtenerPorId(int $id): array|false
    {
        $query = "SELECT 
                    a.id,
                    a.nombre,
                    ta.id AS id_tipo_animal,
                    ta.nombre AS tipo_animal,
                    a.raza,
                    a.descripcion,
                    a.ubicacion,
                    a.contacto,
                    ea.id AS id_estado,
                    ea.nombre AS estado,
                    a.fecha_registro,
                    a.updated_at
                  FROM {$this->table} a
                  INNER JOIN tipos_animal ta ON a.id_tipo_animal = ta.id
                  INNER JOIN estados_animal ea ON a.id_estado = ea.id
                  WHERE a.id = :id";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id', $id, PDO::PARAM_INT);
        $stmt->execute();

        $animal = $stmt->fetch();

        if ($animal) {
            // Obtener todas las imágenes del animal
            $animal['imagenes'] = $this->obtenerImagenes($id);
        }

        return $animal;
    }

    /**
     * Obtener animales filtrados por tipo.
     * 
     * @param int $idTipo ID del tipo de animal.
     * @return array Lista de animales del tipo especificado.
     */
    public function obtenerPorTipo(int $idTipo, int $limite = 10, int $desplazamiento = 0, ?int $idEstado = null): array
    {
        $andEstado = ($idEstado !== null && $idEstado > 0) ? 'AND a.id_estado = :id_estado' : '';

        $query = "SELECT 
                    a.id,
                    a.nombre,
                    ta.id AS id_tipo_animal,
                    ta.nombre AS tipo_animal,
                    a.raza,
                    a.descripcion,
                    a.ubicacion,
                    a.contacto,
                    ea.id AS id_estado,
                    ea.nombre AS estado,
                    a.fecha_registro,
                    COALESCE(a.foto_rehabilitacion, a.foto_ingreso, img.imagen_url) AS imagen_url
                  FROM {$this->table} a
                  INNER JOIN tipos_animal ta ON a.id_tipo_animal = ta.id
                  INNER JOIN estados_animal ea ON a.id_estado = ea.id
                  LEFT JOIN imagenes_animal img ON img.id_animal = a.id AND img.es_principal = 1
                  WHERE a.id_tipo_animal = :id_tipo {$andEstado}
                  ORDER BY a.fecha_registro DESC
                  LIMIT :limite OFFSET :desplazamiento";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id_tipo', $idTipo, PDO::PARAM_INT);
        if ($idEstado !== null && $idEstado > 0) {
            $stmt->bindValue(':id_estado', $idEstado, PDO::PARAM_INT);
        }
        $stmt->bindValue(':limite', $limite, PDO::PARAM_INT);
        $stmt->bindValue(':desplazamiento', $desplazamiento, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    /**
     * Crear un nuevo registro de animal.
     * Usa transacción para insertar animal e imagen en una operación atómica.
     * 
     * @return int ID del animal creado.
     * @throws PDOException Si ocurre un error en la inserción.
     */
    public function crear(): int
    {
        $this->db->beginTransaction();

        try {
            $query = "INSERT INTO {$this->table} 
                        (nombre, id_tipo_animal, raza, descripcion, ubicacion, contacto, id_estado, id_veterinario, foto_ingreso)
                      VALUES 
                        (:nombre, :id_tipo_animal, :raza, :descripcion, :ubicacion, :contacto, :id_estado, :id_veterinario, :foto_ingreso)";

            $stmt = $this->db->prepare($query);
            $stmt->bindValue(':nombre', $this->nombre);
            $stmt->bindValue(':id_tipo_animal', $this->id_tipo_animal, PDO::PARAM_INT);
            $stmt->bindValue(':raza', $this->raza);
            $stmt->bindValue(':descripcion', $this->descripcion);
            $stmt->bindValue(':ubicacion', $this->ubicacion);
            $stmt->bindValue(':contacto', $this->contacto);
            $stmt->bindValue(':id_estado', $this->id_estado, PDO::PARAM_INT);
            $stmt->bindValue(':id_veterinario', $this->id_veterinario);
            $stmt->bindValue(':foto_ingreso', $this->foto_ingreso);
            $stmt->execute();

            $animalId = (int) $this->db->lastInsertId();

            // Si hay imagen, insertarla como principal
            if (!empty($this->imagen_url)) {
                $this->agregarImagen($animalId, $this->imagen_url, true);
            }

            $this->db->commit();

            return $animalId;
        } catch (PDOException $e) {
            $this->db->rollBack();
            throw $e;
        }
    }

    /**
     * Actualizar un registro de animal existente.
     * 
     * @return bool true si se actualizó correctamente.
     */
    public function actualizar(): bool
    {
        $query = "UPDATE {$this->table} SET 
                    nombre = :nombre,
                    id_tipo_animal = :id_tipo_animal,
                    raza = :raza,
                    descripcion = :descripcion,
                    ubicacion = :ubicacion,
                    contacto = :contacto,
                    id_estado = :id_estado
                  WHERE id = :id";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id', $this->id, PDO::PARAM_INT);
        $stmt->bindValue(':nombre', $this->nombre);
        $stmt->bindValue(':id_tipo_animal', $this->id_tipo_animal, PDO::PARAM_INT);
        $stmt->bindValue(':raza', $this->raza);
        $stmt->bindValue(':descripcion', $this->descripcion);
        $stmt->bindValue(':ubicacion', $this->ubicacion);
        $stmt->bindValue(':contacto', $this->contacto);
        $stmt->bindValue(':id_estado', $this->id_estado, PDO::PARAM_INT);

        return $stmt->execute();
    }

    /**
     * Eliminar un animal por su ID.
     * Las imágenes se eliminan automáticamente por ON DELETE CASCADE.
     * 
     * @param int $id ID del animal a eliminar.
     * @return bool true si se eliminó correctamente.
     */
    public function eliminar(int $id): bool
    {
        $query = "DELETE FROM {$this->table} WHERE id = :id";
        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id', $id, PDO::PARAM_INT);

        return $stmt->execute();
    }

    /**
     * Agregar una imagen a un animal.
     * 
     * @param int $idAnimal ID del animal.
     * @param string $imagenUrl URL de la imagen.
     * @param bool $esPrincipal Si es la imagen principal.
     * @return bool true si se insertó correctamente.
     */
    public function agregarImagen(int $idAnimal, string $imagenUrl, bool $esPrincipal = false): bool
    {
        $query = "INSERT INTO imagenes_animal (id_animal, imagen_url, es_principal) 
                  VALUES (:id_animal, :imagen_url, :es_principal)";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id_animal', $idAnimal, PDO::PARAM_INT);
        $stmt->bindValue(':imagen_url', $imagenUrl);
        $stmt->bindValue(':es_principal', $esPrincipal ? 1 : 0, PDO::PARAM_INT);

        return $stmt->execute();
    }

    /**
     * Obtener todas las imágenes de un animal.
     * 
     * @param int $idAnimal ID del animal.
     * @return array Lista de imágenes.
     */
    public function obtenerImagenes(int $idAnimal): array
    {
        $query = "SELECT id, imagen_url, es_principal, created_at 
                  FROM imagenes_animal 
                  WHERE id_animal = :id_animal 
                  ORDER BY es_principal DESC, created_at ASC";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id_animal', $idAnimal, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    /**
     * Obtener todos los animales asignados a un veterinario (cualquier estado).
     * Incluye foto_ingreso y foto_rehabilitacion.
     */
    public function obtenerPorVeterinario(int $idVeterinario): array
    {
        $query = "SELECT 
                    a.id,
                    a.nombre,
                    ta.id AS id_tipo_animal,
                    ta.nombre AS tipo_animal,
                    a.raza,
                    a.descripcion,
                    a.ubicacion,
                    a.contacto,
                    ea.id AS id_estado,
                    ea.nombre AS estado,
                    a.id_veterinario,
                    a.foto_ingreso,
                    a.foto_rehabilitacion,
                    a.fecha_registro,
                    a.updated_at,
                    COALESCE(a.foto_rehabilitacion, a.foto_ingreso, img.imagen_url) AS imagen_url
                  FROM {$this->table} a
                  INNER JOIN tipos_animal ta ON a.id_tipo_animal = ta.id
                  INNER JOIN estados_animal ea ON a.id_estado = ea.id
                  LEFT JOIN imagenes_animal img ON img.id_animal = a.id AND img.es_principal = 1
                  WHERE a.id_veterinario = :id_veterinario
                  ORDER BY a.fecha_registro DESC";

        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id_veterinario', $idVeterinario, PDO::PARAM_INT);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    /**
     * Actualizar únicamente el estado de un animal.
     * Si se cambia a estado 5 (Rehabilitado) y se proporciona foto, la guarda.
     */
    public function actualizarEstado(int $id, int $idEstado, ?string $fotoRehabilitacion = null): bool
    {
$estadosPermitidos = [1, 2, 3, 4, 5, 6];
        if (!in_array($idEstado, $estadosPermitidos, true)) {
            return false;
        }

        if ($idEstado === 5 && $fotoRehabilitacion !== null) {
            $query = "UPDATE {$this->table} SET id_estado = :id_estado, foto_rehabilitacion = :foto_rehabilitacion WHERE id = :id";
            $stmt = $this->db->prepare($query);
            $stmt->bindValue(':foto_rehabilitacion', $fotoRehabilitacion);
        } else {
            $query = "UPDATE {$this->table} SET id_estado = :id_estado WHERE id = :id";
            $stmt = $this->db->prepare($query);
        }

        $stmt->bindValue(':id_estado', $idEstado, PDO::PARAM_INT);
        $stmt->bindValue(':id', $id, PDO::PARAM_INT);

        return $stmt->execute();
    }

    /**
     * Obtener animales activos (estado=1) sin veterinario asignado.
     * Para que el vet pueda tomarlos como pacientes.
     */
    public function obtenerActivosSinVet(): array
    {
        $query = "SELECT 
                    a.id,
                    a.nombre,
                    ta.id AS id_tipo_animal,
                    ta.nombre AS tipo_animal,
                    a.raza,
                    a.descripcion,
                    a.ubicacion,
                    a.contacto,
                    ea.id AS id_estado,
                    ea.nombre AS estado,
                    a.id_veterinario,
                    a.fecha_registro,
                    COALESCE(a.foto_rehabilitacion, a.foto_ingreso, img.imagen_url) AS imagen_url
                  FROM {$this->table} a
                  INNER JOIN tipos_animal ta ON a.id_tipo_animal = ta.id
                  INNER JOIN estados_animal ea ON a.id_estado = ea.id
                  LEFT JOIN imagenes_animal img ON img.id_animal = a.id AND img.es_principal = 1
                  WHERE a.id_estado = 1 AND a.id_veterinario IS NULL
                  ORDER BY a.fecha_registro DESC";

        $stmt = $this->db->prepare($query);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    /**
     * Asignar un veterinario a un animal existente y cambiar estado a 4 (En progreso).
     */
    public function asignarVeterinario(int $idAnimal, int $idVeterinario): bool
    {
        $query = "UPDATE {$this->table} SET id_veterinario = :id_veterinario, id_estado = 4 WHERE id = :id";
        $stmt = $this->db->prepare($query);
        $stmt->bindValue(':id_veterinario', $idVeterinario, PDO::PARAM_INT);
        $stmt->bindValue(':id', $idAnimal, PDO::PARAM_INT);

        return $stmt->execute();
    }
}
