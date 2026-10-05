package com.senati.proyecto_restaurante.data.api

import com.senati.proyecto_restaurante.data.model.LoginRequest
import com.senati.proyecto_restaurante.data.model.LoginResponse
import com.senati.proyecto_restaurante.data.model.Mesa
import com.senati.proyecto_restaurante.data.model.MesaListResponse
import com.senati.proyecto_restaurante.data.model.MesaResponse
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.data.model.PlatoListResponse
import com.senati.proyecto_restaurante.data.model.PlatoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // HU-04: Autenticación
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("status.php")
    suspend fun checkStatus(): Response<Map<String, Any>>

    // HU-05: Platos
    @GET("platos/listar.php")
    suspend fun listarPlatos(): Response<PlatoListResponse>

    @POST("platos/registrar.php")
    suspend fun registrarPlato(@Body plato: Plato): Response<PlatoResponse>

    // HU-06: Mesas
    @GET("mesas/listar.php")
    suspend fun listarMesas(): Response<MesaListResponse>

    @POST("mesas/registrar.php")
    suspend fun registrarMesa(@Body mesa: Mesa): Response<MesaResponse>
}

