package com.example.huellitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.EstadisticasAdminDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EstadoEstadisticas {
    data object Cargando : EstadoEstadisticas()
    data class Exito(val datos: EstadisticasAdminDto) : EstadoEstadisticas()
    data object Error : EstadoEstadisticas()
}

data class RecientesAdmin(
    val enTratamiento: List<AnimalDto> = emptyList(), // estado 4
    val paraAdoptar: List<AnimalDto>   = emptyList(), // estado 6
    val adoptados: List<AnimalDto>     = emptyList()  // estado 2
)

class AdminPanelViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _estadisticas = MutableStateFlow<EstadoEstadisticas>(EstadoEstadisticas.Cargando)
    val estadisticas: StateFlow<EstadoEstadisticas> = _estadisticas.asStateFlow()

    private val _recientes = MutableStateFlow(RecientesAdmin())
    val recientes: StateFlow<RecientesAdmin> = _recientes.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estadisticas.value = EstadoEstadisticas.Cargando
            try {
                // Lanza estadísticas y listas recientes en paralelo
                val statsDeferred = async { api.obtenerEstadisticas() }
                val tratDeferred  = async { api.listarAnimales(limite = 5, estado = 4) }
                val adoptDeferred = async { api.listarAnimales(limite = 5, estado = 6) }
                val adoptadosDeferred = async { api.listarAnimales(limite = 5, estado = 2) }

                val statsResp    = statsDeferred.await()
                val tratResp     = tratDeferred.await()
                val adoptResp    = adoptDeferred.await()
                val adoptadosResp = adoptadosDeferred.await()

                if (statsResp.isSuccessful && statsResp.body()?.status == true) {
                    _estadisticas.value = EstadoEstadisticas.Exito(statsResp.body()!!.data ?: EstadisticasAdminDto())
                } else {
                    _estadisticas.value = EstadoEstadisticas.Error
                }

                _recientes.value = RecientesAdmin(
                    enTratamiento = if (tratResp.isSuccessful) tratResp.body()?.data ?: emptyList() else emptyList(),
                    paraAdoptar   = if (adoptResp.isSuccessful) adoptResp.body()?.data ?: emptyList() else emptyList(),
                    adoptados     = if (adoptadosResp.isSuccessful) adoptadosResp.body()?.data ?: emptyList() else emptyList()
                )
            } catch (e: Exception) {
                _estadisticas.value = EstadoEstadisticas.Error
            }
        }
    }
}

