<?php

/**
 * Archivo de entorno para la configuración de la base de datos.
 * 
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  INSTRUCCIONES:                                                 ║
 * ║  1. En XAMPP local, déjalo tal cual (ya está listo).            ║
 * ║  2. Al subir a HostGator, solo cambia los 4 valores de abajo   ║
 * ║     con los datos que te da cPanel → Bases de datos MySQL.      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 * 
 * EN HOSTGATOR:
 *   - DB_HOST: normalmente 'localhost' (no cambia).
 *   - DB_NAME: en cPanel se crea como "tucuenta_huellitas_db".
 *   - DB_USER: en cPanel se crea como "tucuenta_usuario".
 *   - DB_PASS: la contraseña que asignes en cPanel.
 */

return [
    // ── XAMPP Local (valores actuales) ──
    'DB_HOST'    => 'localhost',
    'DB_NAME'    => 'huellitas_db',
    'DB_USER'    => 'root',
    'DB_PASS'    => '',
    'DB_CHARSET' => 'utf8mb4',

    // ── HostGator (descomenta y reemplaza cuando subas) ──
    // 'DB_HOST'    => 'localhost',
    // 'DB_NAME'    => 'tucuenta_huellitas_db',
    // 'DB_USER'    => 'tucuenta_usuario',
    // 'DB_PASS'    => 'tu_contraseña_segura',
    // 'DB_CHARSET' => 'utf8mb4',
];
