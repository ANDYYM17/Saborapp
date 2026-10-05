package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TopPlato(
    @SerializedName("nombre_plato")
    val nombrePlato: String,
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("total_vendido")
    val totalVendido: Int,
    @SerializedName("total_monto")
    val totalMonto: Double
) : Serializable

data class VentaMesa(
    @SerializedName("numero_mesa")
    val numeroMesa: Int,
    @SerializedName("total_pedidos")
    val totalPedidos: Int,
    @SerializedName("total_mesa")
    val totalMesa: Double
) : Serializable

data class ReporteResumen(
    @SerializedName("venta_total_hoy")
    val ventaTotalHoy: Double = 0.0,
    @SerializedName("cantidad_pedidos_hoy")
    val cantidadPedidosHoy: Int = 0,
    @SerializedName("tiene_ventas_hoy")
    val tieneVentasHoy: Boolean = false,
    @SerializedName("top_platos")
    val topPlatos: List<TopPlato> = emptyList(),
    @SerializedName("ventas_por_mesa")
    val ventasPorMesa: List<VentaMesa> = emptyList()
) : Serializable

data class ReporteResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ReporteResumen?
)
