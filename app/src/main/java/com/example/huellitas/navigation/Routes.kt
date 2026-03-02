package com.example.huellitas.navigation

/**
 * Fuente única de verdad para todas las rutas de navegación.
 * Organizadas por flujo para mayor claridad y mantenibilidad.
 */
object Rutas {

    // ── Pantalla de carga (Lottie preloader) ──
    const val CARGA = "carga"

    // ── Flujo de bienvenida (solo en primer inicio) ──
    const val BIENVENIDA = "bienvenida"
    const val INTRODUCCION = "introduccion"

    // ── Aplicación principal ──
    const val INICIO = "inicio"
    const val REGISTRAR_ANIMAL = "registrar_animal"
}
