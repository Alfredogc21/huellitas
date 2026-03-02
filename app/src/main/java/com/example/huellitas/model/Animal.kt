package com.example.huellitas.model

import java.util.Date

/**
 * Representa un animal callejero registrado en la aplicación.
 * Modelo de datos principal para los listados de animales.
 *
 * @param id Identificador único del animal
 * @param nombre Nombre visible (puede estar vacío si es desconocido)
 * @param tipo Especie/tipo de animal
 * @param raza Raza del animal (puede estar vacía)
 * @param descripcion Notas de comportamiento y salud
 * @param ubicacion Dónde fue visto por última vez
 * @param contacto Cómo contactar a quien lo registró
 * @param imagenUrl URL opcional de la foto del animal
 * @param fechaRegistro Fecha en que fue registrado
 */
data class Animal(
    val id: String,
    val nombre: String,
    val tipo: TipoAnimal,
    val raza: String,
    val descripcion: String,
    val ubicacion: String,
    val contacto: String,
    val imagenUrl: String? = null,
    val fechaRegistro: Date = Date()
)

/**
 * Tipos de animales soportados por la aplicación.
 */
enum class TipoAnimal(val etiqueta: String) {
    PERRO("Perro"),
    GATO("Gato"),
    OTRO("Otro")
}
