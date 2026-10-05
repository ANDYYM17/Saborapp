package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName

data class PlatoResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Plato?
)

data class PlatoListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<Plato> = emptyList()
)

