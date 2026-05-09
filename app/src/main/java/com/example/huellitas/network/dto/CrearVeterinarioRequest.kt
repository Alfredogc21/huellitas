package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

data class CrearVeterinarioRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("especializacion") val especializacion: String,
    @SerializedName("password") val password: String
)
