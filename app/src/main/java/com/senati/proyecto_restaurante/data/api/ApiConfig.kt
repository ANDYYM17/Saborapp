package com.senati.proyecto_restaurante.data.api

/**
 * Configuración centralizada de red y conexión al Backend XAMPP / MySQL.
 * Admite emuladores, conexiones locales y dispositivos físicos (celulares) en la red LAN.
 */
object ApiConfig {
    // URL por defecto
    const val BASE_URL: String = "http://192.168.106.159/saborapp_api/"
    
    // Lista inteligente de URLs candidatas: la app prueba automáticamente cada una
    // hasta conectarse con el servidor XAMPP activo.
    val CANDIDATE_URLS = listOf(
        "http://192.168.106.159/saborapp_api/", // IP PC Servidor (Imagen 1)
        "http://192.168.10.110/saborapp_api/",  // IP Wi-Fi Local (Imagen 2)
        "http://192.168.106.57/saborapp_api/",  // IP Alternativa LAN
        "http://10.0.2.2/saborapp_api/",        // Emulador Android Studio
        "http://127.0.0.1/saborapp_api/",       // Localhost
        "http://172.31.16.1/saborapp_api/"      // Adaptador Virtual
    )
    
    // Timeout ágil para llamadas de red (en segundos)
    const val TIMEOUT_SECONDS: Long = 3L
}
