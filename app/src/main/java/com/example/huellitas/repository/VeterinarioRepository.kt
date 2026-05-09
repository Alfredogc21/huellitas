package com.example.huellitas.repository

import com.example.huellitas.network.RetrofitClient
import com.example.huellitas.network.dto.CrearVeterinarioRequest
import com.example.huellitas.network.dto.VeterinarioDto

class VeterinarioRepository {

    private val api = RetrofitClient.apiService

    suspend fun listarVeterinarios(): Resultado<List<VeterinarioDto>> {
        return try {
            val response = api.listarVeterinarios()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status) {
                    Resultado.Exito(body.data ?: emptyList())
                } else {
                    Resultado.Error(response.body()?.message ?: "Error al obtener veterinarios.")
                }
            } else {
                Resultado.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            Resultado.Error("Sin conexión. Verifica tu red.")
        }
    }

    suspend fun crearVeterinario(request: CrearVeterinarioRequest): Resultado<VeterinarioDto> {
        return try {
            val response = api.crearVeterinario(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status && body.data != null) {
                    Resultado.Exito(body.data)
                } else {
                    Resultado.Error(body?.message ?: "Error al registrar veterinario.")
                }
            } else {
                Resultado.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            Resultado.Error("Sin conexión. Verifica tu red.")
        }
    }
}
