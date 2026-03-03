package com.example.huellitas.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellitas.model.Animal
import com.example.huellitas.model.TipoAnimal
import com.example.huellitas.repository.AnimalRepository
import com.example.huellitas.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de UI para la pantalla de registro de animal.
 */
sealed class EstadoRegistro {
    data object Inactivo : EstadoRegistro()
    data object Enviando : EstadoRegistro()
    data class Exito(val animal: Animal) : EstadoRegistro()
    data class Error(val mensaje: String) : EstadoRegistro()
}

/**
 * ViewModel para PantallaRegistroAnimal.
 * Gestiona el envío del formulario y la subida de imagen.
 * Extiende AndroidViewModel para acceder al Context (necesario para leer el Uri de la imagen).
 */
class AnimalRegistroViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AnimalRepository()

    private val _estadoRegistro = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Inactivo)
    val estadoRegistro: StateFlow<EstadoRegistro> = _estadoRegistro.asStateFlow()

    /** Uri de la imagen seleccionada por el usuario */
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    /**
     * Establece la imagen seleccionada por el usuario.
     */
    fun seleccionarImagen(uri: Uri?) {
        _imagenUri.value = uri
    }

    /**
     * Envía el formulario al backend.
     * Si hay imagen, primero la sube y luego crea el animal con la URL.
     */
    fun registrarAnimal(
        nombre: String,
        tipo: TipoAnimal,
        raza: String,
        descripcion: String,
        ubicacion: String,
        contacto: String
    ) {
        if (_imagenUri.value == null) {
            _estadoRegistro.value = EstadoRegistro.Error("La foto es obligatoria.")
            return
        }
        if (descripcion.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("La descripción es obligatoria.")
            return
        }
        if (ubicacion.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("La ubicación es obligatoria.")
            return
        }
        if (contacto.isBlank()) {
            _estadoRegistro.value = EstadoRegistro.Error("El teléfono o email de contacto es obligatorio.")
            return
        }

        val idTipo = when (tipo) {
            TipoAnimal.PERRO -> 1
            TipoAnimal.GATO  -> 2
            TipoAnimal.OTRO  -> 3
        }

        viewModelScope.launch {
            _estadoRegistro.value = EstadoRegistro.Enviando

            // Paso 1: Subir imagen si hay una seleccionada
            var imagenUrl: String? = null
            val uriImagen = _imagenUri.value

            if (uriImagen != null) {
                when (val resultadoImagen = repository.subirImagen(getApplication(), uriImagen)) {
                    is Resultado.Exito -> imagenUrl = resultadoImagen.datos
                    is Resultado.Error -> {
                        _estadoRegistro.value = EstadoRegistro.Error(resultadoImagen.mensaje)
                        return@launch
                    }
                }
            }

            // Paso 2: Crear el animal con la URL de la imagen (si se subió)
            _estadoRegistro.value = when (
                val resultado = repository.crearAnimal(
                    nombre = nombre,
                    idTipoAnimal = idTipo,
                    raza = raza,
                    descripcion = descripcion,
                    ubicacion = ubicacion,
                    contacto = contacto,
                    imagenUrl = imagenUrl
                )
            ) {
                is Resultado.Exito -> EstadoRegistro.Exito(resultado.datos)
                is Resultado.Error -> EstadoRegistro.Error(resultado.mensaje)
            }
        }
    }

    /**
     * Reinicia el estado para permitir un nuevo registro.
     */
    fun reiniciarEstado() {
        _estadoRegistro.value = EstadoRegistro.Inactivo
    }
}
