-- ==========================================================
-- Basify Export
-- Base de datos : huellitas_db
-- Motor         : MariaDB
-- Generado      : 9/5/2026, 5:41:45 p. m.
-- ==========================================================

-- CREATE DATABASE IF NOT EXISTS `huellitas_db`;
-- USE `huellitas_db`;

-- ==========================================================
-- Estructura de tablas
-- ==========================================================

-- Tabla: estados_animal
CREATE TABLE IF NOT EXISTS `estados_animal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,    -- Clave primaria
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla: roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,    -- Clave primaria
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Tabla: tipos_animal
CREATE TABLE IF NOT EXISTS `tipos_animal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,    -- Clave primaria
  `nombre` varchar(50) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla: usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,    -- Clave primaria
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `especializacion` varchar(150) DEFAULT NULL,
  `password` varchar(255) NOT NULL COMMENT 'Hash bcrypt de la contrase├▒a',
  `rol_id` int(11) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`),
  KEY `idx_usuarios_correo` (`correo`),
  KEY `idx_usuarios_rol` (`rol_id`),
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`) ON UPDATE CASCADE    -- FK -> roles.id
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Tabla: animales
CREATE TABLE IF NOT EXISTS `animales` (
  `id` int(11) NOT NULL AUTO_INCREMENT,    -- Clave primaria
  `nombre` varchar(100) DEFAULT NULL COMMENT 'Puede ser NULL si el animal no tiene nombre conocido',
  `id_tipo_animal` int(11) NOT NULL,
  `raza` varchar(100) DEFAULT NULL,
  `descripcion` text DEFAULT NULL COMMENT 'Notas sobre comportamiento y salud',
  `ubicacion` varchar(255) NOT NULL COMMENT 'Última ubicación conocida',
  `contacto` varchar(255) NOT NULL COMMENT 'Datos de contacto de quien registra',
  `id_estado` int(11) NOT NULL DEFAULT 1,
  `id_usuario` int(11) DEFAULT NULL COMMENT 'NULL = registrado por usuario publico, valor = admin autenticado',
  `id_veterinario` int(11) DEFAULT NULL COMMENT 'Veterinario asignado al tratamiento del animal',
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_animales_tipo` (`id_tipo_animal`),
  KEY `idx_animales_estado` (`id_estado`),
  KEY `idx_animales_fecha` (`fecha_registro`),
  KEY `idx_animales_usuario` (`id_usuario`),
  KEY `idx_animales_veterinario` (`id_veterinario`),
  CONSTRAINT `fk_animal_estado` FOREIGN KEY (`id_estado`) REFERENCES `estados_animal` (`id`) ON UPDATE CASCADE,    -- FK -> estados_animal.id
  CONSTRAINT `fk_animal_tipo` FOREIGN KEY (`id_tipo_animal`) REFERENCES `tipos_animal` (`id`) ON UPDATE CASCADE,    -- FK -> tipos_animal.id
  CONSTRAINT `fk_animal_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,    -- FK -> usuarios.id
  CONSTRAINT `fk_animal_veterinario` FOREIGN KEY (`id_veterinario`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE    -- FK -> usuarios.id
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Tabla: imagenes_animal
CREATE TABLE IF NOT EXISTS `imagenes_animal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,    -- Clave primaria
  `id_animal` int(11) NOT NULL,
  `imagen_url` varchar(500) NOT NULL,
  `es_principal` tinyint(1) DEFAULT 0 COMMENT '1 = imagen principal, 0 = secundaria',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_imagenes_animal` (`id_animal`),
  CONSTRAINT `fk_imagen_animal` FOREIGN KEY (`id_animal`) REFERENCES `animales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE    -- FK -> animales.id
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;








-- ==========================================================
-- Fin del script
-- ==========================================================
