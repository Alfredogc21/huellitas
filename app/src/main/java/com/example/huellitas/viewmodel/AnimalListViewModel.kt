package com.example.huellitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.model.Animal
import com.example.huellitas.repository.AnimalRepository
import com.example.huellitas.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de UI para la pantalla de lista de animales.
 */
sealed class EstadoListaAnimales {
    data object Cargando : EstadoListaAnimales()
    data class Exito(val animales: List<Animal>) : EstadoListaAnimales()
    data class Error(val mensaje: String) : EstadoListaAnimales()
}

/**
 * ViewModel para PantallaListaAnimales.
 * Gestiona la carga y refresco de animales desde la API.
 */
class AnimalListViewModel(
    private val repository: AnimalRepository = AnimalRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoListaAnimales>(EstadoListaAnimales.Cargando)
    val estado: StateFlow<EstadoListaAnimales> = _estado.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        cargarAnimales()
    }

    /**
     * Carga todos los animales desde el servidor.
     * Muestra el estado Cargando (pantalla completa con spinner).
     */
    fun cargarAnimales() {
        viewModelScope.launch {
            _estado.value = EstadoListaAnimales.Cargando
            _estado.value = when (val resultado = repository.obtenerAnimales()) {
                is Resultado.Exito -> EstadoListaAnimales.Exito(resultado.datos)
                is Resultado.Error -> EstadoListaAnimales.Error(resultado.mensaje)
            }
        }
    }

    /**
     * Recarga la lista sin ocultar el contenido actual (para pull-to-refresh).
     * Activa el indicador de refresco sin cambiar al estado Cargando.
     */
    fun refrescar() {
        viewModelScope.launch {
            _isRefreshing.value = true
            when (val resultado = repository.obtenerAnimales()) {
                is Resultado.Exito -> _estado.value = EstadoListaAnimales.Exito(resultado.datos)
                is Resultado.Error -> _estado.value = EstadoListaAnimales.Error(resultado.mensaje)
            }
            _isRefreshing.value = false
        }
    }

    /**
     * Filtra animales por tipo (1=Perro, 2=Gato, 3=Otro).
     */
    fun filtrarPorTipo(idTipo: Int) {
        viewModelScope.launch {
            _estado.value = EstadoListaAnimales.Cargando
            _estado.value = when (val resultado = repository.obtenerAnimalesPorTipo(idTipo)) {
                is Resultado.Exito -> EstadoListaAnimales.Exito(resultado.datos)
                is Resultado.Error -> EstadoListaAnimales.Error(resultado.mensaje)
            }
        }
    }
}
