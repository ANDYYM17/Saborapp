package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName

data class MesaResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Mesa?
)

data class MesaListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<Mesa> = emptyList()
)

