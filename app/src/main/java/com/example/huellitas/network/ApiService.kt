package com.example.huellitas.network

import com.example.huellitas.network.dto.ActualizarEstadoRequest
import com.example.huellitas.network.dto.AnimalDto
import com.example.huellitas.network.dto.ApiResponse
import com.example.huellitas.network.dto.AsignarVetRequest
import com.example.huellitas.network.dto.CrearAnimalRequest
import com.example.huellitas.network.dto.CrearVeterinarioRequest
import com.example.huellitas.network.dto.LoginRequest
import com.example.huellitas.network.dto.RegistrarPacienteVetRequest
import com.example.huellitas.network.dto.RegistroUsuarioRequest
import com.example.huellitas.network.dto.UsuarioDto
import com.example.huellitas.network.dto.VeterinarioDto
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
        @Query("limite") limite: Int = 10,
        @Query("estado") estado: Int? = null
    ): Response<ApiResponse<List<AnimalDto>>>

    @GET("api/animales/listar.php")
    suspend fun listarAnimalesPorTipo(
        @Query("tipo") idTipo: Int,
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 10,
        @Query("estado") estado: Int? = null
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
        @Part("tipo") tipo: RequestBody? = null,
        @Part("id_animal") idAnimal: RequestBody? = null
    ): Response<ApiResponse<Map<String, String>>>

    // ── Autenticación ──

    @POST("api/auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<UsuarioDto>>

    @POST("api/auth/registrar.php")
    suspend fun registrarUsuario(
        @Body request: RegistroUsuarioRequest
    ): Response<ApiResponse<UsuarioDto>>

    // ── Veterinarios ──

    @GET("api/veterinarios/listar.php")
    suspend fun listarVeterinarios(): Response<ApiResponse<List<VeterinarioDto>>>

    @POST("api/veterinarios/crear.php")
    suspend fun crearVeterinario(
        @Body request: CrearVeterinarioRequest
    ): Response<ApiResponse<VeterinarioDto>>

    @GET("api/animales/mis_animales_vet.php")
    suspend fun misAnimalesVet(
        @Query("id_veterinario") idVeterinario: Int
    ): Response<ApiResponse<List<AnimalDto>>>

    @POST("api/animales/actualizar_estado.php")
    suspend fun actualizarEstadoAnimal(
        @Body request: ActualizarEstadoRequest
    ): Response<ApiResponse<AnimalDto>>

    @GET("api/animales/listar_sin_vet.php")
    suspend fun listarAnimalesSinVet(): Response<ApiResponse<List<AnimalDto>>>

    @POST("api/animales/registrar_paciente_vet.php")
    suspend fun registrarPacienteVet(
        @Body request: RegistrarPacienteVetRequest
    ): Response<ApiResponse<AnimalDto>>

    @POST("api/animales/asignar_vet.php")
    suspend fun asignarVet(
        @Body request: AsignarVetRequest
    ): Response<ApiResponse<AnimalDto>>
}
