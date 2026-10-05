package com.senati.proyecto_restaurante.data.repository

import com.senati.proyecto_restaurante.data.api.ApiConfig
import com.senati.proyecto_restaurante.data.api.RetrofitClient
import com.senati.proyecto_restaurante.data.model.AgregarItemRequest
import com.senati.proyecto_restaurante.data.model.MesaIdRequest
import com.senati.proyecto_restaurante.data.model.Pedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PedidoRepository {

    suspend fun obtenerPedidoPorMesa(idMesa: Int): Result<Pedido?> {
        return withContext(Dispatchers.IO) {
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.obtenerPedidoPorMesa(idMesa)

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success) {
                            return@withContext Result.success(body.data)
                        }
                    }
                } catch (_: Exception) {
                }
            }
            Result.failure(Exception("Error al consultar el pedido de la mesa"))
        }
    }

    suspend fun agregarItem(idMesa: Int, idPlato: Int, cantidad: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            val request = AgregarItemRequest(idMesa = idMesa, idPlato = idPlato, cantidad = cantidad)
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.agregarItemPedido(request)

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        return@withContext if (body.success) {
                            Result.success(body.message)
                        } else {
                            Result.failure(Exception(body.message))
                        }
                    }
                } catch (_: Exception) {
                }
            }
            Result.failure(Exception("Error de conexión al agregar item al pedido"))
        }
    }

    suspend fun cerrarCuenta(idMesa: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            val request = MesaIdRequest(idMesa = idMesa)
            for (url in ApiConfig.CANDIDATE_URLS) {
                try {
                    val service = RetrofitClient.getClient(url)
                    val response = service.cerrarCuenta(request)

                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        return@withContext if (body.success) {
                            Result.success(body.message)
                        } else {
                            Result.failure(Exception(body.message))
                        }
                    }
                } catch (_: Exception) {
                }
            }
            Result.failure(Exception("Error de conexión al cerrar la cuenta"))
        }
    }
}
