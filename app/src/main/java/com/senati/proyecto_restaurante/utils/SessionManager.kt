package com.senati.proyecto_restaurante.utils

import android.content.Context
import android.content.SharedPreferences
import com.senati.proyecto_restaurante.data.model.Usuario

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "saborapp_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
    }

    fun saveUser(usuario: Usuario) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, usuario.id)
            .putString(KEY_USERNAME, usuario.usuario)
            .putString(KEY_ROLE, usuario.rol)
            .apply()
    }

    fun getUser(): Usuario? {
        if (!isLoggedIn()) return null
        val id = prefs.getInt(KEY_USER_ID, -1)
        val username = prefs.getString(KEY_USERNAME, "") ?: ""
        val role = prefs.getString(KEY_ROLE, "") ?: ""
        return if (id != -1 && username.isNotEmpty()) {
            Usuario(id, username, role)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

