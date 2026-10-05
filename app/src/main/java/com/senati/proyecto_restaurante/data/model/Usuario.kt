package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Usuario(
    @SerializedName("id")
    val id: Int,
    @SerializedName("usuario")
    val usuario: String,
    @SerializedName("rol")
    val rol: String
) : Serializable

