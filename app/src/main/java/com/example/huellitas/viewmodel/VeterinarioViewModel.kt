package com.example.huellitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.network.dto.CrearVeterinarioRequest
import com.example.huellitas.network.dto.VeterinarioDto
import com.example.huellitas.repository.Resultado
import com.example.huellitas.repository.VeterinarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EstadoVeterinarios {
    data object Cargando : EstadoVeterinarios()
    data class Exito(val veterinarios: List<VeterinarioDto>) : EstadoVeterinarios()
    data class Error(val mensaje: String) : EstadoVeterinarios()
}

sealed class EstadoCrearVet {
    data object Inactivo : EstadoCrearVet()
    data object Cargando : EstadoCrearVet()
    data object Exito : EstadoCrearVet()
    data class Error(val mensaje: String) : EstadoCrearVet()
}

class VeterinarioViewModel(
    private val repository: VeterinarioRepository = VeterinarioRepository()
) : ViewModel() {

    private val _estadoLista = MutableStateFlow<EstadoVeterinarios>(EstadoVeterinarios.Cargando)
    val estadoLista: StateFlow<EstadoVeterinarios> = _estadoLista.asStateFlow()

    private val _estadoCrear = MutableStateFlow<EstadoCrearVet>(EstadoCrearVet.Inactivo)
    val estadoCrear: StateFlow<EstadoCrearVet> = _estadoCrear.asStateFlow()

    init {
        cargarVeterinarios()
    }

    fun cargarVeterinarios() {
        viewModelScope.launch {
            _estadoLista.value = EstadoVeterinarios.Cargando
            when (val resultado = repository.listarVeterinarios()) {
                is Resultado.Exito -> _estadoLista.value = EstadoVeterinarios.Exito(resultado.datos)
                is Resultado.Error -> _estadoLista.value = EstadoVeterinarios.Error(resultado.mensaje)
            }
        }
    }

    fun crearVeterinario(
        nombre: String,
        telefono: String,
        correo: String,
        especializacion: String,
        password: String
    ) {
        viewModelScope.launch {
            _estadoCrear.value = EstadoCrearVet.Cargando
            val request = CrearVeterinarioRequest(
                nombre = nombre.trim(),
                telefono = telefono.trim(),
                correo = correo.trim(),
                especializacion = especializacion.trim(),
                password = password
            )
            when (val resultado = repository.crearVeterinario(request)) {
                is Resultado.Exito -> {
                    _estadoCrear.value = EstadoCrearVet.Exito
                    cargarVeterinarios()
                }
                is Resultado.Error -> _estadoCrear.value = EstadoCrearVet.Error(resultado.mensaje)
            }
        }
    }

    fun resetearEstadoCrear() {
        _estadoCrear.value = EstadoCrearVet.Inactivo
    }
}
