package com.senati.proyecto_restaurante.data.api

/**
 * Configuración centralizada de red y conexión al Backend XAMPP / MySQL.
 * Permite cambiar fácilmente el host o puerto sin modificar las Activities.
 */
object ApiConfig {
    // 10.0.2.2 es la dirección estándar del emulador de Android Studio para acceder a localhost en la PC
    const val BASE_URL: String = "http://10.0.2.2/saborapp_api/"
    
    // Lista de endpoints candidatos por si el emulador usa puente de red o IP local
    val CANDIDATE_URLS = listOf(
        "http://10.0.2.2/saborapp_api/",
        "http://192.168.10.110/saborapp_api/",
        "http://192.168.106.159/saborapp_api/",
        "http://192.168.106.57/saborapp_api/",
        "http://127.0.0.1/saborapp_api/"
    )
    
    // Timeout ágil para llamadas de red (en segundos)
    const val TIMEOUT_SECONDS: Long = 3L
}
