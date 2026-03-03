package com.example.huellitas.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.huellitas.model.Animal
import com.example.huellitas.model.TipoAnimal
import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.CrearAnimalRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
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
    suspend fun obtenerAnimales(pagina: Int = 1, limite: Int = 10): Resultado<List<Animal>> {
        return try {
            val response = api.listarAnimales(pagina = pagina, limite = limite)
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
    suspend fun obtenerAnimalesPorTipo(idTipo: Int, pagina: Int = 1, limite: Int = 10): Resultado<List<Animal>> {
        return try {
            val response = api.listarAnimalesPorTipo(idTipo, pagina = pagina, limite = limite)
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

            // Comprimir imagen antes de subir para acelerar la carga
            val bytes = comprimirImagen(inputStream.readBytes())
            inputStream.close()

            val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipart = MultipartBody.Part.createFormData(
                "imagen",
                "foto_animal.jpg",
                requestBody
            )

            val response = api.subirImagen(multipart)
            if (response.isSuccessful && response.body()?.status == true) {
                val url = response.body()!!.data?.get("imagen_url")
                    ?: return Resultado.Error("No se recibió la URL de la imagen.")
                Resultado.Exito(url)
            } else {
                // Extraer mensaje real del servidor para diagnóstico
                val mensajeServidor = response.body()?.message
                    ?: try {
                        response.errorBody()?.string()?.take(200)
                    } catch (_: Exception) { null }
                Resultado.Error(mensajeServidor ?: "Error al subir la imagen (HTTP ${response.code()})")
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

    /**
     * Comprime una imagen a JPEG redimensionando a máximo 1280px de lado mayor
     * y calidad 75%. Reduce de ~5-10MB (CameraX) a ~100-300KB.
     */
    private fun comprimirImagen(bytesOriginal: ByteArray): ByteArray {
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytesOriginal, 0, bytesOriginal.size, opciones)

        val anchoOriginal = opciones.outWidth
        val altoOriginal = opciones.outHeight
        val ladoMax = 1280

        // Calcular inSampleSize para reducir memoria al decodificar
        var inSampleSize = 1
        if (anchoOriginal > ladoMax || altoOriginal > ladoMax) {
            val mitadAncho = anchoOriginal / 2
            val mitadAlto = altoOriginal / 2
            while (mitadAncho / inSampleSize >= ladoMax && mitadAlto / inSampleSize >= ladoMax) {
                inSampleSize *= 2
            }
        }

        val opcionesDeco = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        val bitmap = BitmapFactory.decodeByteArray(bytesOriginal, 0, bytesOriginal.size, opcionesDeco)
            ?: return bytesOriginal

        // Escalar al tamaño final exacto si sigue siendo mayor a ladoMax
        val bitmapFinal = if (bitmap.width > ladoMax || bitmap.height > ladoMax) {
            val escala = ladoMax.toFloat() / maxOf(bitmap.width, bitmap.height)
            val nuevoAncho = (bitmap.width * escala).toInt()
            val nuevoAlto = (bitmap.height * escala).toInt()
            val escalado = Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)
            if (escalado !== bitmap) bitmap.recycle()
            escalado
        } else {
            bitmap
        }

        val salida = ByteArrayOutputStream()
        bitmapFinal.compress(Bitmap.CompressFormat.JPEG, 75, salida)
        bitmapFinal.recycle()
        return salida.toByteArray()
    }
}
