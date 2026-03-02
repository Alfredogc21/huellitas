package com.example.huellitas.model

/**
 * Defines how the animal list can be sorted/filtered.
 *
 * - [RECENT]: Shows only animals from the last 7 days, newest first
 * - [BY_DATE]: Shows all animals sorted by date, newest first
 * - [OLDEST]: Shows all animals sorted by date, oldest first
 */
enum class SortOption(val label: String) {
    RECENT("Recientes"),
    BY_DATE("Por fecha"),
    OLDEST("Más antiguos")
}
