package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

data class ActualizarEstadoRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("id_estado") val idEstado: Int,
    @SerializedName("foto_rehabilitacion") val fotoRehabilitacion: String? = null
)
