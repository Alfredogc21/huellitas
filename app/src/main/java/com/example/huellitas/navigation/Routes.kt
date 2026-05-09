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
    const val TUTORIAL = "tutorial"
    const val REGISTRAR_ANIMAL = "registrar_animal"

    // ── Flujo de administración ──
    const val ADMIN_LOGIN = "admin_login"
    const val ADMIN_REGISTRO = "admin_registro"
    const val ADMIN_TUTORIAL = "admin_tutorial"
    const val ADMIN_PANEL = "admin_panel"

    // ── Panel Veterinario ──
    const val VET_PANEL = "vet_panel/{vetId}"
    fun vetPanel(vetId: Int) = "vet_panel/$vetId"

    const val VET_REGISTRAR_PACIENTE = "vet_registrar_paciente/{vetId}"
    fun vetRegistrarPaciente(vetId: Int) = "vet_registrar_paciente/$vetId"

    // ── Adopción ──
    const val ADOPCION = "adopcion"

    // ── Gestión de Veterinarios (admin) ──
    const val ADMIN_VETERINARIOS = "admin_veterinarios"
    const val ADMIN_AGREGAR_VET = "admin_agregar_vet"
}
