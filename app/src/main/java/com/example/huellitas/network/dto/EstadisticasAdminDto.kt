package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

data class EstadisticasAdminDto(
    @SerializedName("total")         val total: Int = 0,
    @SerializedName("activos")       val activos: Int = 0,
    @SerializedName("adoptados")     val adoptados: Int = 0,
    @SerializedName("inactivos")     val inactivos: Int = 0,
    @SerializedName("en_progreso")   val enProgreso: Int = 0,
    @SerializedName("rehabilitados") val rehabilitados: Int = 0,
    @SerializedName("para_adoptar")  val paraAdoptar: Int = 0
)
