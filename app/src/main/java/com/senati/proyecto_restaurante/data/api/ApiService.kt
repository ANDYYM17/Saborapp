package com.senati.proyecto_restaurante.data.api

import com.senati.proyecto_restaurante.data.model.AgregarItemRequest
import com.senati.proyecto_restaurante.data.model.GenericActionResponse
import com.senati.proyecto_restaurante.data.model.LoginRequest
import com.senati.proyecto_restaurante.data.model.LoginResponse
import com.senati.proyecto_restaurante.data.model.Mesa
import com.senati.proyecto_restaurante.data.model.MesaIdRequest
import com.senati.proyecto_restaurante.data.model.MesaListResponse
import com.senati.proyecto_restaurante.data.model.MesaResponse
import com.senati.proyecto_restaurante.data.model.PedidoResponse
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.data.model.PlatoIdRequest
import com.senati.proyecto_restaurante.data.model.PlatoListResponse
import com.senati.proyecto_restaurante.data.model.PlatoResponse
import com.senati.proyecto_restaurante.data.model.ReporteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // HU-04: Autenticación
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("status.php")
    suspend fun checkStatus(): Response<Map<String, Any>>

    // HU-05 / HU-07: Platos
    @GET("platos/listar.php")
    suspend fun listarPlatos(
        @Query("search") search: String? = null,
        @Query("solo_disponibles") soloDisponibles: Int? = null
    ): Response<PlatoListResponse>

    @POST("platos/registrar.php")
    suspend fun registrarPlato(@Body plato: Plato): Response<PlatoResponse>

    @POST("platos/actualizar.php")
    suspend fun actualizarPlato(@Body plato: Plato): Response<PlatoResponse>

    @POST("platos/eliminar.php")
    suspend fun eliminarPlato(@Body request: PlatoIdRequest): Response<GenericActionResponse>

    // HU-06: Mesas
    @GET("mesas/listar.php")
    suspend fun listarMesas(): Response<MesaListResponse>

    @POST("mesas/registrar.php")
    suspend fun registrarMesa(@Body mesa: Mesa): Response<MesaResponse>

    // HU-08 / HU-09: Pedidos y Cuentas
    @GET("pedidos/obtener_por_mesa.php")
    suspend fun obtenerPedidoPorMesa(@Query("id_mesa") idMesa: Int): Response<PedidoResponse>

    @POST("pedidos/agregar_item.php")
    suspend fun agregarItemPedido(@Body request: AgregarItemRequest): Response<GenericActionResponse>

    @POST("pedidos/cerrar_cuenta.php")
    suspend fun cerrarCuenta(@Body request: MesaIdRequest): Response<GenericActionResponse>

    // HU-10: Reportes
    @GET("reportes/resumen.php")
    suspend fun obtenerReportes(): Response<ReporteResponse>
}
