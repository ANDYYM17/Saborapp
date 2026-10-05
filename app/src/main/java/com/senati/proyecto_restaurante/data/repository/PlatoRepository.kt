package com.senati.proyecto_restaurante.data.repository

import com.senati.proyecto_restaurante.data.api.ApiConfig
import com.senati.proyecto_restaurante.data.api.RetrofitClient
import com.senati.proyecto_restaurante.data.model.Plato
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlatoRepository {

    suspend fun listarPlatos(): Result<List<Plato>> {
        return withContext(Dispatchers.IO) {
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.listarPlatos()

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
            Result.failure(Exception("No se pudieron cargar los platos desde el servidor MySQL"))
        }
    }

    suspend fun registrarPlato(plato: Plato): Result<Plato> {
        return withContext(Dispatchers.IO) {
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.registrarPlato(plato)

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
            Result.failure(Exception("Error de conexión al registrar el plato"))
        }
    }
}

