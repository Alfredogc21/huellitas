package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

data class AsignarVetRequest(
    @SerializedName("id_animal")      val idAnimal: Int,
    @SerializedName("id_veterinario") val idVeterinario: Int
)
