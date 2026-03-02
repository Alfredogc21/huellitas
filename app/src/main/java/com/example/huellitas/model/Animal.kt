package com.example.huellitas.model

import java.util.Date

/**
 * Represents a stray animal registered in the app.
 * This is the core data model for animal listings.
 *
 * @param id Unique identifier for the animal
 * @param name Display name (may be empty if unknown)
 * @param type The species/type of animal
 * @param breed The breed of the animal (may be empty)
 * @param description Behavioral and health notes
 * @param location Where the animal was last seen
 * @param contact How to reach the person who registered it
 * @param imageUrl Optional photo URL
 * @param registrationDate When the animal was registered
 */
data class Animal(
    val id: String,
    val name: String,
    val type: AnimalType,
    val breed: String,
    val description: String,
    val location: String,
    val contact: String,
    val imageUrl: String? = null,
    val registrationDate: Date = Date()
)

/**
 * Types of animals supported by the app.
 */
enum class AnimalType(val label: String) {
    DOG("Perro"),
    CAT("Gato"),
    OTHER("Otro")
}
