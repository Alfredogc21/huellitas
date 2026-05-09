package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

data class VeterinarioDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("especializacion") val especializacion: String?,
    @SerializedName("rol_id") val rolId: Int,
    @SerializedName("rol") val rol: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
