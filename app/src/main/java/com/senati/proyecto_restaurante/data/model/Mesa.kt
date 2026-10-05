package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Mesa(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("numero")
    val numero: Int,
    @SerializedName("capacidad")
    val capacidad: Int,
    @SerializedName("estado")
    val estado: String = "LIBRE"
) : Serializable

