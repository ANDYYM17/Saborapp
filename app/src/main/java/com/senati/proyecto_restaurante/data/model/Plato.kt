package com.senati.proyecto_restaurante.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Plato(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("precio")
    val precio: Double,
    @SerializedName("disponible")
    val disponible: Int = 1
) : Serializable

