package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("usuario")
    val usuario: String,
    @SerializedName("clave")
    val clave: String
)

