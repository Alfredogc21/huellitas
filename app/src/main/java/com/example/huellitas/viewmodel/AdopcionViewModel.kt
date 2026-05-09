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

sealed class EstadoAdopcion {
    data object Cargando : EstadoAdopcion()
    data class Exito(val animales: List<Animal>) : EstadoAdopcion()
    data class Error(val mensaje: String) : EstadoAdopcion()
}

class AdopcionViewModel(
    private val repository: AnimalRepository = AnimalRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoAdopcion>(EstadoAdopcion.Cargando)
    val estado: StateFlow<EstadoAdopcion> = _estado.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private var todosLosAnimales: List<Animal> = emptyList()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = EstadoAdopcion.Cargando
            when (val resultado = repository.obtenerAnimalesAdopcion()) {
                is Resultado.Exito -> {
                    todosLosAnimales = resultado.datos
                    _estado.value = EstadoAdopcion.Exito(aplicarBusqueda(resultado.datos))
                }
                is Resultado.Error -> _estado.value = EstadoAdopcion.Error(resultado.mensaje)
            }
        }
    }

    fun refrescar() {
        viewModelScope.launch {
            _isRefreshing.value = true
            when (val resultado = repository.obtenerAnimalesAdopcion()) {
                is Resultado.Exito -> {
                    todosLosAnimales = resultado.datos
                    _estado.value = EstadoAdopcion.Exito(aplicarBusqueda(resultado.datos))
                }
                is Resultado.Error -> { /* mantener lista actual */ }
            }
            _isRefreshing.value = false
        }
    }

    fun buscar(query: String) {
        _busqueda.value = query
        val actual = ((_estado.value as? EstadoAdopcion.Exito)?.animales) ?: return
        _estado.value = EstadoAdopcion.Exito(aplicarBusqueda(todosLosAnimales))
    }

    private fun aplicarBusqueda(lista: List<Animal>): List<Animal> {
        val q = _busqueda.value.trim().lowercase()
        if (q.isEmpty()) return lista
        return lista.filter { animal ->
            animal.nombre.lowercase().contains(q) ||
                animal.raza.lowercase().contains(q) ||
                animal.descripcion.lowercase().contains(q)
        }
    }
}
