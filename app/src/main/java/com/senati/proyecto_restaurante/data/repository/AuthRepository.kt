package com.senati.proyecto_restaurante.data.repository

import com.senati.proyecto_restaurante.data.api.ApiConfig
import com.senati.proyecto_restaurante.data.api.RetrofitClient
import com.senati.proyecto_restaurante.data.model.LoginRequest
import com.senati.proyecto_restaurante.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    suspend fun login(usuario: String, clave: String): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            val request = LoginRequest(usuario = usuario, clave = clave)
            var lastErrorMessage: String? = null

            // HU-04: Autenticación 100% real consultando la base de datos MySQL mediante la API
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.login(request)

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        return@withContext if (body.success && body.data != null) {
                            Result.success(body.data)
                        } else {
                            Result.failure(Exception(body.message.ifEmpty { "Credenciales incorrectas" }))
                        }
                    } else if (response.code() == 401 || response.code() == 400) {
                        return@withContext Result.failure(Exception("Credenciales incorrectas"))
                    }
                } catch (e: Exception) {
                    lastErrorMessage = e.message
                    // Probar siguiente endpoint candidato
                }
            }

            Result.failure(Exception(lastErrorMessage ?: "Error al conectar con la base de datos MySQL"))
        }
    }
}

