package com.example.huellitas.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta del endpoint listar_rehabilitados_admin.php.
 * Contiene dos listas separadas:
 *   - rehabilitados: animales con estado=5 (pendientes de aprobación del admin)
 *   - para_adoptar:  animales con estado=6 (aprobados y visibles en el feed público)
 */
data class RehabilitadosAdminDto(
    @SerializedName("rehabilitados") val rehabilitados: List<AnimalDto> = emptyList(),
    @SerializedName("para_adoptar")  val paraAdoptar: List<AnimalDto> = emptyList()
)
