package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Pedido(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_mesa")
    val idMesa: Int,
    @SerializedName("fecha")
    val fecha: String? = null,
    @SerializedName("estado")
    val estado: String = "ABIERTO",
    @SerializedName("total")
    val total: Double = 0.0,
    @SerializedName("detalles")
    val detalles: List<DetallePedido> = emptyList()
) : Serializable
