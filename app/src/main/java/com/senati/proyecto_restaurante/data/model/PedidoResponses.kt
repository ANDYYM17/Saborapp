package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName

data class PedidoResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Pedido?
)

data class AgregarItemRequest(
    @SerializedName("id_mesa")
    val idMesa: Int,
    @SerializedName("id_plato")
    val idPlato: Int,
    @SerializedName("cantidad")
    val cantidad: Int
)

data class GenericActionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Map<String, Any>? = null
)

data class MesaIdRequest(
    @SerializedName("id_mesa")
    val idMesa: Int
)

data class PlatoIdRequest(
    @SerializedName("id")
    val id: Int
)
