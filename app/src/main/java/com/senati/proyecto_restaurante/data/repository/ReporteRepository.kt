package com.senati.proyecto_restaurante.data.repository

import com.senati.proyecto_restaurante.data.api.ApiConfig
import com.senati.proyecto_restaurante.data.api.RetrofitClient
import com.senati.proyecto_restaurante.data.model.ReporteResumen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReporteRepository {

    suspend fun obtenerReportes(): Result<ReporteResumen> {
        return withContext(Dispatchers.IO) {
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.obtenerReportes()

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success && body.data != null) {
                            return@withContext Result.success(body.data)
                        }
                    }
                } catch (_: Exception) {
                }
            }
            Result.failure(Exception("Error al cargar los reportes de ventas"))
        }
    }
}
