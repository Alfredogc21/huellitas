package com.example.huellitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.ActualizarEstadoRequest
import com.example.huellitas.network.dto.AnimalDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la pantalla de adopciones del admin. */
sealed class EstadoAdopcionAdmin {
    data object Cargando : EstadoAdopcionAdmin()
    data class Exito(
        val rehabilitados: List<AnimalDto>,  // estado=5: pendientes de aprobación
        val paraAdoptar: List<AnimalDto>     // estado=6: aprobados, visibles en feed
    ) : EstadoAdopcionAdmin()
    data class Error(val mensaje: String) : EstadoAdopcionAdmin()
}

/** Estado de una acción puntual (aprobar / marcar adoptado). */
sealed class EstadoAccionAdmin {
    data object Inactivo : EstadoAccionAdmin()
    data object Cargando : EstadoAccionAdmin()
    data object Exito : EstadoAccionAdmin()
    data class Error(val mensaje: String) : EstadoAccionAdmin()
}

class AdopcionAdminViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _estado = MutableStateFlow<EstadoAdopcionAdmin>(EstadoAdopcionAdmin.Cargando)
    val estado: StateFlow<EstadoAdopcionAdmin> = _estado.asStateFlow()

    private val _estadoAccion = MutableStateFlow<EstadoAccionAdmin>(EstadoAccionAdmin.Inactivo)
    val estadoAccion: StateFlow<EstadoAccionAdmin> = _estadoAccion.asStateFlow()

    init {
        cargar()
    }

    /** Carga las dos listas desde el backend. */
    fun cargar() {
        viewModelScope.launch {
            _estado.value = EstadoAdopcionAdmin.Cargando
            try {
                val response = api.listarRehabilitadosAdmin()
                if (response.isSuccessful && response.body()?.status == true) {
                    val data = response.body()!!.data
                    _estado.value = EstadoAdopcionAdmin.Exito(
                        rehabilitados = data?.rehabilitados ?: emptyList(),
                        paraAdoptar   = data?.paraAdoptar   ?: emptyList()
                    )
                } else {
                    _estado.value = EstadoAdopcionAdmin.Error(
                        response.body()?.message ?: "Error al cargar animales."
                    )
                }
            } catch (e: Exception) {
                _estado.value = EstadoAdopcionAdmin.Error("Sin conexión.")
            }
        }
    }

    /**
     * El admin aprueba un animal rehabilitado (estado 5 → 6 "Para adoptar").
     * Una vez aprobado aparecerá en el feed público con la etiqueta "Adoptame".
     */
    fun aprobarParaAdopcion(idAnimal: Int) {
        cambiarEstado(idAnimal, 6)
    }

    /**
     * El admin marca un animal como adoptado (estado 6 → 2 "Adoptado").
     * El animal desaparece del feed público.
     */
    fun marcarAdoptado(idAnimal: Int) {
        cambiarEstado(idAnimal, 2)
    }

    private fun cambiarEstado(idAnimal: Int, nuevoEstado: Int) {
        viewModelScope.launch {
            _estadoAccion.value = EstadoAccionAdmin.Cargando
            try {
                val response = api.actualizarEstadoAnimal(
                    ActualizarEstadoRequest(idAnimal, nuevoEstado)
                )
                if (response.isSuccessful && response.body()?.status == true) {
                    _estadoAccion.value = EstadoAccionAdmin.Exito
                    cargar() // recarga ambas listas
                } else {
                    _estadoAccion.value = EstadoAccionAdmin.Error(
                        response.body()?.message ?: "Error al actualizar estado."
                    )
                }
            } catch (e: Exception) {
                _estadoAccion.value = EstadoAccionAdmin.Error("Sin conexión.")
            }
        }
    }

    fun resetearAccion() {
        _estadoAccion.value = EstadoAccionAdmin.Inactivo
    }
}
