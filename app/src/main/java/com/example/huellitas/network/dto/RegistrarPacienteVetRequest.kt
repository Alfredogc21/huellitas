package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

data class RegistrarPacienteVetRequest(
    @SerializedName("nombre")         val nombre: String?,
    @SerializedName("id_tipo_animal") val idTipoAnimal: Int = 1,
    @SerializedName("raza")           val raza: String?,
    @SerializedName("descripcion")    val descripcion: String?,
    @SerializedName("ubicacion")      val ubicacion: String,
    @SerializedName("contacto")       val contacto: String,
    @SerializedName("id_veterinario") val idVeterinario: Int,
    @SerializedName("foto_ingreso")   val fotoIngreso: String? = null
)
