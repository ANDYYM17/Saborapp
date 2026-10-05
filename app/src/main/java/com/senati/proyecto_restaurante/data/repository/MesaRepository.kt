package com.senati.proyecto_restaurante.data.repository

import com.senati.proyecto_restaurante.data.api.ApiConfig
import com.senati.proyecto_restaurante.data.api.RetrofitClient
import com.senati.proyecto_restaurante.data.model.Mesa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MesaRepository {

    suspend fun listarMesas(): Result<List<Mesa>> {
        return withContext(Dispatchers.IO) {
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.listarMesas()

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success) {
                            return@withContext Result.success(body.data)
                        }
                    }
                } catch (_: Exception) {
                    // Continuar con el siguiente endpoint candidato
                }
            }
            Result.failure(Exception("No se pudieron cargar las mesas desde el servidor MySQL"))
        }
    }

    suspend fun registrarMesa(mesa: Mesa): Result<Mesa> {
        return withContext(Dispatchers.IO) {
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.registrarMesa(mesa)

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        return@withContext if (body.success && body.data != null) {
                            Result.success(body.data)
                        } else {
                            Result.failure(Exception(body.message))
                        }
                    }
                } catch (_: Exception) {
                    // Continuar con el siguiente endpoint candidato
                }
            }
            Result.failure(Exception("Error de conexión al registrar la mesa"))
        }
    }
}

