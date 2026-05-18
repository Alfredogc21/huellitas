-- ==========================================================
-- Basify Export
-- Base de datos : huellitas_db
-- Motor         : MariaDB
-- Generado      : 9/5/2026, 5:42:30 p. m.
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
-- Datos
-- ==========================================================

-- Registros de: estados_animal (6)
INSERT INTO `estados_animal` (`id`, `nombre`, `descripcion`, `created_at`) VALUES
  (1, 'Activo', 'El animal está registrado y visible en la aplicación', '2026-03-02 23:36:06'),
  (2, 'Adoptado', 'El animal fue adoptado exitosamente', '2026-03-02 23:36:06'),
  (3, 'Inactivo', 'El registro fue desactivado', '2026-03-02 23:36:06'),
  (4, 'En progreso', 'Animal asignado al ßrea mÚdica para tratamiento veterinario', '2026-05-09 05:16:09'),
  (5, 'Rehabilitado', 'Animal rehabilitado y disponible para adopci¾n', '2026-05-09 05:16:09'),
  (6, 'Para adoptar', 'Animal rehabilitado y aprobado por el admin para adopción pública', '2026-05-09 20:07:19');

-- Registros de: roles (2)
INSERT INTO `roles` (`id`, `nombre`, `descripcion`, `created_at`) VALUES
  (1, 'Admin', 'Administrador con acceso completo al panel de gesti├│n', '2026-04-09 10:35:34'),
  (2, 'Veterinario', 'Veterinario con acceso al panel mÚdico de pacientes', '2026-05-09 05:16:09');

-- Registros de: tipos_animal (3)
INSERT INTO `tipos_animal` (`id`, `nombre`, `created_at`) VALUES
  (1, 'Perro', '2026-03-02 23:36:06'),
  (2, 'Gato', '2026-03-02 23:36:06'),
  (3, 'Otro', '2026-03-02 23:36:06');

-- Registros de: usuarios (1)
INSERT INTO `usuarios` (`id`, `nombre`, `apellidos`, `correo`, `telefono`, `especializacion`, `password`, `rol_id`, `created_at`, `updated_at`) VALUES
  (1, 'Ivon Sofia', 'Chaves', 'ivon@gmail.com', NULL, NULL, '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, '2026-04-09 10:35:34', '2026-04-09 11:03:01');

-- Registros de: animales (7)
INSERT INTO `animales` (`id`, `nombre`, `id_tipo_animal`, `raza`, `descripcion`, `ubicacion`, `contacto`, `id_estado`, `id_usuario`, `id_veterinario`, `fecha_registro`, `updated_at`) VALUES
  (1, NULL, 1, NULL, 'Se encuentra herido, como si hubiera tenido una pelea', 'Lo vi cerca de la alcaldía', '3111117739', 1, NULL, NULL, '2026-03-03 01:17:58', '2026-03-03 01:17:58'),
  (2, 'Tobby', 1, 'Criollo', 'Se ve desnutrido', 'Calle 14 N17-20', '3146789062', 1, NULL, NULL, '2026-03-03 02:37:56', '2026-03-03 02:37:56'),
  (3, NULL, 1, NULL, 'Se ve que el perro está golpeado', 'Parque principal', '3157827514', 1, NULL, NULL, '2026-03-03 05:05:31', '2026-03-03 20:06:39'),
  (4, NULL, 1, NULL, 'Perro abandonado cerca de las maquinarias de fundadores', 'Barrio fundadores', '3157836528', 1, NULL, NULL, '2026-03-03 05:33:30', '2026-03-03 20:06:47'),
  (5, NULL, 1, NULL, 'Se ve sucio pero esta desorientado', 'Puente Chaparral', '3158930752', 1, NULL, NULL, '2026-03-03 20:18:38', '2026-03-03 20:18:38'),
  (6, NULL, 1, NULL, 'se encuentra perro en malas condiciones físicas (urgente)', 'calle 1 carrera 9na, Espinal', '11111111', 1, NULL, NULL, '2026-03-18 00:36:39', '2026-03-18 00:36:39'),
  (7, NULL, 1, NULL, 'Perro herido (sangrando) pata izquierda', 'calle #18 carrera 9', '11111111', 1, NULL, NULL, '2026-03-18 00:38:49', '2026-03-18 00:38:49');

-- Registros de: imagenes_animal (7)
INSERT INTO `imagenes_animal` (`id`, `id_animal`, `imagen_url`, `es_principal`, `created_at`) VALUES
  (1, 1, 'https://alfreweb.com/huellitas/uploads/animal_69a636c61cc2e3.01461096.jpg', 1, '2026-03-03 01:17:58'),
  (2, 2, 'https://alfreweb.com/huellitas/uploads/animal_69a64984296952.94358829.jpg', 1, '2026-03-03 02:37:56'),
  (3, 3, 'https://alfreweb.com/huellitas/uploads/animal_69a636c61cc2e3.01461096.jpg', 1, '2026-03-03 05:05:31'),
  (4, 4, 'https://alfreweb.com/huellitas/uploads/animal_69a64984296952.94358829.jpg', 1, '2026-03-03 05:33:30'),
  (5, 5, 'https://alfreweb.com/huellitas/uploads/animal_69a7421e52dc20.27616159.jpg', 1, '2026-03-03 20:18:38'),
  (6, 6, 'https://alfreweb.com/huellitas/uploads/animal_69b9f396f2b4b6.97553290.jpg', 1, '2026-03-18 00:36:39'),
  (7, 7, 'https://alfreweb.com/huellitas/uploads/animal_69b9f4190b87d3.38003747.jpg', 1, '2026-03-18 00:38:49');

-- ==========================================================
-- Fin del script
-- ==========================================================
