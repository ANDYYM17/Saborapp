package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetallePedido(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_pedido")
    val idPedido: Int = 0,
    @SerializedName("id_plato")
    val idPlato: Int,
    @SerializedName("nombre_plato")
    val nombrePlato: String? = null,
    @SerializedName("categoria_plato")
    val categoriaPlato: String? = null,
    @SerializedName("cantidad")
    val cantidad: Int,
    @SerializedName("precio_unit")
    val precioUnit: Double,
    @SerializedName("subtotal")
    val subtotal: Double
) : Serializable
