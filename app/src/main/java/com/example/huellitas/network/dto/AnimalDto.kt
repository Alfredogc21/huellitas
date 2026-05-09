package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO que representa la respuesta JSON de la API PHP para un animal.
 * Los nombres usan snake_case para coincidir exactamente con el JSON del backend.
 */
data class AnimalDto(
    @SerializedName("id")            val id: Int,
    @SerializedName("nombre")        val nombre: String?,
    @SerializedName("id_tipo_animal") val idTipoAnimal: Int,
    @SerializedName("tipo_animal")   val tipoAnimal: String,
    @SerializedName("raza")          val raza: String?,
    @SerializedName("descripcion")   val descripcion: String?,
    @SerializedName("ubicacion")     val ubicacion: String,
    @SerializedName("contacto")      val contacto: String,
    @SerializedName("id_estado")     val idEstado: Int,
    @SerializedName("estado")        val estado: String,
    @SerializedName("fecha_registro") val fechaRegistro: String,
    @SerializedName("imagen_url")    val imagenUrl: String?,
    @SerializedName("id_veterinario") val idVeterinario: Int? = null,
    @SerializedName("foto_ingreso")   val fotoIngreso: String? = null,
    @SerializedName("foto_rehabilitacion") val fotoRehabilitacion: String? = null
)
