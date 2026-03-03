package com.example.huellitas.network

import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.ApiResponse
import com.example.huellitas.network.dto.CrearAnimalRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Interfaz de Retrofit que mapea todos los endpoints del backend PHP.
 * Cada función corresponde a un archivo PHP dentro de /api/.
 */
interface ApiService {

    @GET("api/animales/listar.php")
    suspend fun listarAnimales(
        @Query("orden") orden: String = "fecha_registro",
        @Query("direccion") direccion: String = "DESC",
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 10
    ): Response<ApiResponse<List<AnimalDto>>>

    @GET("api/animales/listar.php")
    suspend fun listarAnimalesPorTipo(
        @Query("tipo") idTipo: Int,
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 10
    ): Response<ApiResponse<List<AnimalDto>>>

    @POST("api/animales/crear.php")
    suspend fun crearAnimal(
        @Body request: CrearAnimalRequest
    ): Response<ApiResponse<AnimalDto>>

    /**
     * Sube una imagen al servidor.
     * POST /api/animales/subir_imagen.php (multipart/form-data)
     */
    @Multipart
    @POST("api/animales/subir_imagen.php")
    suspend fun subirImagen(
        @Part imagen: MultipartBody.Part,
        @Part("id_animal") idAnimal: RequestBody? = null
    ): Response<ApiResponse<Map<String, String>>>
}
