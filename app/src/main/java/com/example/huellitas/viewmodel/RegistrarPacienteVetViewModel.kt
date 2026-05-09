package com.example.huellitas.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.AsignarVetRequest
import com.example.huellitas.network.dto.RegistrarPacienteVetRequest
import com.example.huellitas.repository.AnimalRepository
import com.example.huellitas.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EstadoAnimalesDisponibles {
    data object Cargando : EstadoAnimalesDisponibles()
    data class Exito(val animales: List<AnimalDto>) : EstadoAnimalesDisponibles()
    data class Error(val mensaje: String) : EstadoAnimalesDisponibles()
}

sealed class EstadoRegistroPaciente {
    data object Inactivo : EstadoRegistroPaciente()
    data object Enviando : EstadoRegistroPaciente()
    data object Exito : EstadoRegistroPaciente()
    data class Error(val mensaje: String) : EstadoRegistroPaciente()
}

class RegistrarPacienteVetViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.apiService
    private val repository = AnimalRepository()

    private val _estadoDisponibles = MutableStateFlow<EstadoAnimalesDisponibles>(EstadoAnimalesDisponibles.Cargando)
    val estadoDisponibles: StateFlow<EstadoAnimalesDisponibles> = _estadoDisponibles.asStateFlow()

    private val _estadoRegistro = MutableStateFlow<EstadoRegistroPaciente>(EstadoRegistroPaciente.Inactivo)
    val estadoRegistro: StateFlow<EstadoRegistroPaciente> = _estadoRegistro.asStateFlow()

    // URI de foto de ingreso seleccionada
    private val _fotoIngresoUri = MutableStateFlow<Uri?>(null)
    val fotoIngresoUri: StateFlow<Uri?> = _fotoIngresoUri.asStateFlow()

    init {
        cargarDisponibles()
    }

    fun cargarDisponibles() {
        viewModelScope.launch {
            _estadoDisponibles.value = EstadoAnimalesDisponibles.Cargando
            try {
                val response = api.listarAnimalesSinVet()
                if (response.isSuccessful && response.body()?.status == true) {
                    _estadoDisponibles.value = EstadoAnimalesDisponibles.Exito(
                        response.body()?.data ?: emptyList()
                    )
                } else {
                    _estadoDisponibles.value = EstadoAnimalesDisponibles.Error(
                        response.body()?.message ?: "Error al cargar animales."
                    )
                }
            } catch (e: Exception) {
                _estadoDisponibles.value = EstadoAnimalesDisponibles.Error("Sin conexión.")
            }
        }
    }

    fun seleccionarFotoIngreso(uri: Uri?) {
        _fotoIngresoUri.value = uri
    }

    /** Asigna un animal existente al veterinario (estado 4) */
    fun asignarAnimalExistente(idAnimal: Int, idVeterinario: Int) {
        viewModelScope.launch {
            _estadoRegistro.value = EstadoRegistroPaciente.Enviando
            try {
                val response = api.asignarVet(AsignarVetRequest(idAnimal, idVeterinario))
                _estadoRegistro.value = if (response.isSuccessful && response.body()?.status == true) {
                    EstadoRegistroPaciente.Exito
                } else {
                    EstadoRegistroPaciente.Error(
                        response.body()?.message ?: "Error al asignar animal."
                    )
                }
            } catch (e: Exception) {
                _estadoRegistro.value = EstadoRegistroPaciente.Error("Sin conexión.")
            }
        }
    }

    /** Registra un animal nuevo directamente por el veterinario */
    fun registrarNuevoPaciente(
        vetId: Int,
        nombre: String,
        raza: String,
        descripcion: String,
        ubicacion: String,
        contacto: String
    ) {
        if (ubicacion.isBlank()) {
            _estadoRegistro.value = EstadoRegistroPaciente.Error("La ubicación es obligatoria.")
            return
        }
        viewModelScope.launch {
            _estadoRegistro.value = EstadoRegistroPaciente.Enviando

            // Subir foto de ingreso si hay una seleccionada
            var fotoIngresoUrl: String? = null
            val uri = _fotoIngresoUri.value
            if (uri != null) {
                when (val r = repository.subirImagen(getApplication(), uri, "tratamiento")) {
                    is Resultado.Exito -> fotoIngresoUrl = r.datos
                    is Resultado.Error -> { /* continuar sin foto */ }
                }
            }

            try {
                val response = api.registrarPacienteVet(
                    RegistrarPacienteVetRequest(
                        nombre         = nombre.trim().ifBlank { null },
                        idTipoAnimal   = 1,
                        raza           = raza.trim().ifBlank { null },
                        descripcion    = descripcion.trim().ifBlank { null },
                        ubicacion      = ubicacion.trim(),
                        contacto       = contacto.trim(),
                        idVeterinario  = vetId,
                        fotoIngreso    = fotoIngresoUrl
                    )
                )
                _estadoRegistro.value = if (response.isSuccessful && response.body()?.status == true) {
                    EstadoRegistroPaciente.Exito
                } else {
                    EstadoRegistroPaciente.Error(
                        response.body()?.message ?: "Error al registrar paciente."
                    )
                }
            } catch (e: Exception) {
                _estadoRegistro.value = EstadoRegistroPaciente.Error("Sin conexión.")
            }
        }
    }

    fun resetearEstado() {
        _estadoRegistro.value = EstadoRegistroPaciente.Inactivo
    }
}
