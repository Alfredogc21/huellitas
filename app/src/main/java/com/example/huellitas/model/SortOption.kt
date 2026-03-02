package com.example.huellitas.model

/**
 * Opciones de filtrado y ordenamiento para la lista de animales.
 *
 * - [RECIENTES]: Ordena de más reciente a más antiguo
 * - [POR_FECHA]: Permite seleccionar una fecha específica en el calendario
 * - [ANTIGUOS]: Ordena de más antiguo a más reciente
 */
enum class OpcionFiltro(val etiqueta: String) {
    RECIENTES("Recientes"),
    POR_FECHA("Por fecha"),
    ANTIGUOS("Más antiguos")
}
