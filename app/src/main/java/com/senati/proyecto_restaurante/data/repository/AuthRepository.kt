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

            // Intentar primero conectarse a los endpoints de XAMPP
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
                    // Continuar con el siguiente endpoint candidato
                }
            }

            // Si los endpoints de red no responden (ej. firewall o emulador sin puente de red),
            // se valida contra los usuarios iniciales definidos para Sprint 1
            if (usuario.equals("admin", ignoreCase = true) && clave == "1234") {
                Result.success(Usuario(id = 1, usuario = "admin", rol = "ADMIN"))
            } else if (usuario.equals("mozo1", ignoreCase = true) && clave == "1234") {
                Result.success(Usuario(id = 2, usuario = "mozo1", rol = "MOZO"))
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        }
    }
}
