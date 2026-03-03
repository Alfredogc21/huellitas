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
 * Gestiona la carga paginada, refresco y filtro por tipo de animales desde la API.
 */
class AnimalListViewModel(
    private val repository: AnimalRepository = AnimalRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoListaAnimales>(EstadoListaAnimales.Cargando)
    val estado: StateFlow<EstadoListaAnimales> = _estado.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /** Tipo de animal seleccionado: null = Todos */
    private val _tipoSeleccionado = MutableStateFlow<Int?>(null)
    val tipoSeleccionado: StateFlow<Int?> = _tipoSeleccionado.asStateFlow()

    private val _isCargandoMas = MutableStateFlow(false)
    val isCargandoMas: StateFlow<Boolean> = _isCargandoMas.asStateFlow()

    private var paginaActual = 1
    private val limitePorPagina = 10
    private var hayMasPaginas = true
    private var animalesAcumulados = mutableListOf<Animal>()

    init {
        cargarAnimales()
    }

    /**
     * Carga la primera página de animales (muestra spinner pantalla completa).
     */
    fun cargarAnimales() {
        viewModelScope.launch {
            _estado.value = EstadoListaAnimales.Cargando
            paginaActual = 1
            hayMasPaginas = true
            animalesAcumulados.clear()

            val resultado = fetchPagina(1)
            when (resultado) {
                is Resultado.Exito -> {
                    animalesAcumulados.addAll(resultado.datos)
                    hayMasPaginas = resultado.datos.size >= limitePorPagina
                    _estado.value = EstadoListaAnimales.Exito(animalesAcumulados.toList())
                }
                is Resultado.Error -> _estado.value = EstadoListaAnimales.Error(resultado.mensaje)
            }
        }
    }

    /**
     * Carga la siguiente página (infinite scroll).
     * No hace nada si ya se está cargando o no hay más páginas.
     */
    fun cargarMas() {
        if (_isCargandoMas.value || !hayMasPaginas) return

        viewModelScope.launch {
            _isCargandoMas.value = true
            val siguiente = paginaActual + 1

            when (val resultado = fetchPagina(siguiente)) {
                is Resultado.Exito -> {
                    paginaActual = siguiente
                    animalesAcumulados.addAll(resultado.datos)
                    hayMasPaginas = resultado.datos.size >= limitePorPagina
                    _estado.value = EstadoListaAnimales.Exito(animalesAcumulados.toList())
                }
                is Resultado.Error -> { /* No mostrar error, simplemente no agrega más */ }
            }
            _isCargandoMas.value = false
        }
    }

    /**
     * Recarga desde la primera página sin ocultar el contenido actual (pull-to-refresh).
     */
    fun refrescar() {
        viewModelScope.launch {
            _isRefreshing.value = true
            paginaActual = 1
            hayMasPaginas = true

            when (val resultado = fetchPagina(1)) {
                is Resultado.Exito -> {
                    animalesAcumulados.clear()
                    animalesAcumulados.addAll(resultado.datos)
                    hayMasPaginas = resultado.datos.size >= limitePorPagina
                    _estado.value = EstadoListaAnimales.Exito(animalesAcumulados.toList())
                }
                is Resultado.Error -> _estado.value = EstadoListaAnimales.Error(resultado.mensaje)
            }
            _isRefreshing.value = false
        }
    }

    /**
     * Cambia el filtro de tipo de animal y recarga desde la primera página.
     * @param idTipo null = Todos, 1 = Perro, 2 = Gato, 3 = Otro
     */
    fun seleccionarTipo(idTipo: Int?) {
        if (_tipoSeleccionado.value == idTipo) return
        _tipoSeleccionado.value = idTipo
        cargarAnimales()
    }

    /** Llama a la API según el tipo seleccionado y la página indicada. */
    private suspend fun fetchPagina(pagina: Int): Resultado<List<Animal>> {
        val tipo = _tipoSeleccionado.value
        return if (tipo != null) {
            repository.obtenerAnimalesPorTipo(tipo, pagina = pagina, limite = limitePorPagina)
        } else {
            repository.obtenerAnimales(pagina = pagina, limite = limitePorPagina)
        }
    }
}
