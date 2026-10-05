package com.senati.proyecto_restaurante.data.api

import com.senati.proyecto_restaurante.data.model.LoginRequest
import com.senati.proyecto_restaurante.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("status.php")
    suspend fun checkStatus(): Response<Map<String, Any>>
}

