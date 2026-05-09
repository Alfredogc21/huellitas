package com.example.huellitas.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.ActualizarEstadoRequest
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.repository.AnimalRepository
import com.example.huellitas.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EstadoPanelVet {
    data object Cargando : EstadoPanelVet()
    data class Exito(
        val enTratamiento: List<AnimalDto>,
        val rehabilitados: List<AnimalDto>
    ) : EstadoPanelVet()
    data class Error(val mensaje: String) : EstadoPanelVet()
}

sealed class EstadoCambioEstado {
    data object Inactivo : EstadoCambioEstado()
    data object Cargando : EstadoCambioEstado()
    data object Exito : EstadoCambioEstado()
    data class Error(val mensaje: String) : EstadoCambioEstado()
}

class PanelVetViewModel(application: Application, private val vetId: Int) : AndroidViewModel(application) {

    private val api = RetrofitClient.apiService
    private val repository = AnimalRepository()

    private val _estadoAnimales = MutableStateFlow<EstadoPanelVet>(EstadoPanelVet.Cargando)
    val estadoAnimales: StateFlow<EstadoPanelVet> = _estadoAnimales.asStateFlow()

    private val _estadoCambio = MutableStateFlow<EstadoCambioEstado>(EstadoCambioEstado.Inactivo)
    val estadoCambio: StateFlow<EstadoCambioEstado> = _estadoCambio.asStateFlow()

    init {
        cargarAnimales()
    }

    fun cargarAnimales() {
        if (vetId <= 0) {
            _estadoAnimales.value = EstadoPanelVet.Exito(emptyList(), emptyList())
            return
        }
        viewModelScope.launch {
            _estadoAnimales.value = EstadoPanelVet.Cargando
            try {
                val response = api.misAnimalesVet(vetId)
                if (response.isSuccessful && response.body()?.status == true) {
                    val todos = response.body()?.data ?: emptyList()
                    _estadoAnimales.value = EstadoPanelVet.Exito(
                        enTratamiento = todos.filter { it.idEstado == 4 },
                        rehabilitados  = todos.filter { it.idEstado == 5 }
                    )
                } else {
                    _estadoAnimales.value = EstadoPanelVet.Error(
                        response.body()?.message ?: "Error al cargar pacientes."
                    )
                }
            } catch (e: Exception) {
                _estadoAnimales.value = EstadoPanelVet.Error("Sin conexión. Verifica tu red.")
            }
        }
    }

    /**
     * Marca un animal como Rehabilitado (estado 5).
     * Si se proporciona una foto, la sube primero y la guarda como foto_rehabilitacion.
     */
    fun marcarParaAdopcion(idAnimal: Int, fotoUri: Uri? = null) {
        viewModelScope.launch {
            _estadoCambio.value = EstadoCambioEstado.Cargando
            try {
                var fotoUrl: String? = null
                if (fotoUri != null) {
                    when (val r = repository.subirImagen(getApplication(), fotoUri, "rehabilitado")) {
                        is Resultado.Exito -> fotoUrl = r.datos
                        is Resultado.Error -> { /* continuar sin foto */ }
                    }
                }
                val response = api.actualizarEstadoAnimal(
                    ActualizarEstadoRequest(idAnimal, 5, fotoUrl)
                )
                if (response.isSuccessful && response.body()?.status == true) {
                    _estadoCambio.value = EstadoCambioEstado.Exito
                    cargarAnimales()
                } else {
                    _estadoCambio.value = EstadoCambioEstado.Error(
                        response.body()?.message ?: "Error al actualizar estado."
                    )
                }
            } catch (e: Exception) {
                _estadoCambio.value = EstadoCambioEstado.Error("Sin conexión. Verifica tu red.")
            }
        }
    }

    fun resetearEstadoCambio() {
        _estadoCambio.value = EstadoCambioEstado.Inactivo
    }
}

