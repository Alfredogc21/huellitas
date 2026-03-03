package com.example.huellitas.repository

import android.content.Context
import android.net.Uri
import com.example.huellitas.model.Animal
import com.example.huellitas.model.TipoAnimal
import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.CrearAnimalRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Resultado sellado que representa el resultado de una operación de red.
 * Permite manejar éxito y error sin excepciones en la UI.
 */
sealed class Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}

/**
 * Repositorio de animales. Actúa como fuente única de datos.
 * Realiza las llamadas a la API y mapea los DTOs al modelo de dominio.
 */
class AnimalRepository {

    private val api = RetrofitClient.apiService
    private val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * Obtiene todos los animales del servidor, ordenados por fecha descendente.
     */
    suspend fun obtenerAnimales(): Resultado<List<Animal>> {
        return try {
            val response = api.listarAnimales()
            if (response.isSuccessful && response.body()?.status == true) {
                val lista = response.body()!!.data?.map { it.aModelo() } ?: emptyList()
                Resultado.Exito(lista)
            } else {
                Resultado.Error(response.body()?.message ?: "Error al obtener animales")
            }
        } catch (e: Exception) {
            Resultado.Error("Sin conexión: ${e.localizedMessage}")
        }
    }

    /**
     * Obtiene animales filtrados por tipo.
     * @param idTipo 1=Perro, 2=Gato, 3=Otro
     */
    suspend fun obtenerAnimalesPorTipo(idTipo: Int): Resultado<List<Animal>> {
        return try {
            val response = api.listarAnimalesPorTipo(idTipo)
            if (response.isSuccessful && response.body()?.status == true) {
                val lista = response.body()!!.data?.map { it.aModelo() } ?: emptyList()
                Resultado.Exito(lista)
            } else {
                Resultado.Error(response.body()?.message ?: "Error al filtrar animales")
            }
        } catch (e: Exception) {
            Resultado.Error("Sin conexión: ${e.localizedMessage}")
        }
    }

    /**
     * Registra un nuevo animal en el servidor.
     */
    suspend fun crearAnimal(
        nombre: String?,
        idTipoAnimal: Int,
        raza: String?,
        descripcion: String?,
        ubicacion: String,
        contacto: String,
        imagenUrl: String? = null
    ): Resultado<Animal> {
        return try {
            val request = CrearAnimalRequest(
                nombre = nombre?.takeIf { it.isNotBlank() },
                idTipoAnimal = idTipoAnimal,
                raza = raza?.takeIf { it.isNotBlank() },
                descripcion = descripcion?.takeIf { it.isNotBlank() },
                ubicacion = ubicacion,
                contacto = contacto,
                imagenUrl = imagenUrl
            )
            val response = api.crearAnimal(request)
            if (response.isSuccessful && response.body()?.status == true) {
                val dto = response.body()!!.data!!
                Resultado.Exito(dto.aModelo())
            } else {
                Resultado.Error(response.body()?.message ?: "Error al registrar el animal")
            }
        } catch (e: Exception) {
            Resultado.Error("Sin conexión: ${e.localizedMessage}")
        }
    }

    /**
     * Sube una imagen al servidor desde un Uri del dispositivo.
     * Soporta tanto URIs content:// (galería) como file:// (cámara interna CameraX).
     * Devuelve la URL pública de la imagen.
     */
    suspend fun subirImagen(context: Context, uri: Uri): Resultado<String> {
        return try {
            val contentResolver = context.contentResolver

            // Para URIs file:// (fotos tomadas con CameraX guardadas en cacheDir)
            // el ContentResolver no funciona de forma fiable; usamos FileInputStream directo.
            val inputStream = if (uri.scheme == "file") {
                val archivo = java.io.File(requireNotNull(uri.path) { "Ruta de archivo nula" })
                java.io.FileInputStream(archivo)
            } else {
                contentResolver.openInputStream(uri)
            } ?: return Resultado.Error("No se pudo leer la imagen seleccionada.")

            // Determinar MIME type: para file:// siempre jpg (CameraX guarda .jpg)
            val mimeType = if (uri.scheme == "file") {
                "image/jpeg"
            } else {
                contentResolver.getType(uri) ?: "image/jpeg"
            }

            val bytes = inputStream.readBytes()
            inputStream.close()

            val extension = when (mimeType) {
                "image/png"  -> "png"
                "image/webp" -> "webp"
                else         -> "jpg"
            }
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val multipart = MultipartBody.Part.createFormData(
                "imagen",
                "foto_animal.$extension",
                requestBody
            )

            val response = api.subirImagen(multipart)
            if (response.isSuccessful && response.body()?.status == true) {
                val url = response.body()!!.data?.get("imagen_url")
                    ?: return Resultado.Error("No se recibió la URL de la imagen.")
                Resultado.Exito(url)
            } else {
                Resultado.Error(response.body()?.message ?: "Error al subir la imagen")
            }
        } catch (e: Exception) {
            Resultado.Error("Error al subir imagen: ${e.localizedMessage}")
        }
    }

    // ── Mapeo de DTO al modelo de dominio ──────────────────────────────────

    private fun AnimalDto.aModelo(): Animal {
        val tipo = when (tipoAnimal.lowercase(Locale.getDefault())) {
            "perro" -> TipoAnimal.PERRO
            "gato"  -> TipoAnimal.GATO
            else    -> TipoAnimal.OTRO
        }
        val fecha: Date = try {
            formatoFecha.parse(fechaRegistro) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        return Animal(
            id = id.toString(),
            nombre = nombre ?: "",
            tipo = tipo,
            raza = raza ?: "",
            descripcion = descripcion ?: "",
            ubicacion = ubicacion,
            contacto = contacto,
            imagenUrl = imagenUrl,
            fechaRegistro = fecha
        )
    }
}
