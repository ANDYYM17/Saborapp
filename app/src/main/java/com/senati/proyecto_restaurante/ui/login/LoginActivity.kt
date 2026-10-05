package com.senati.proyecto_restaurante.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Usuario
import com.senati.proyecto_restaurante.data.repository.AuthRepository
import com.senati.proyecto_restaurante.databinding.ActivityLoginBinding
import com.senati.proyecto_restaurante.ui.menu.MenuActivity
import com.senati.proyecto_restaurante.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // HU-12: Auto-login si la sesión ya fue guardada previamente
        if (sessionManager.isLoggedIn()) {
            val user = sessionManager.getUser()
            if (user != null) {
                val intent = Intent(this, MenuActivity::class.java).apply {
                    putExtra(MenuActivity.EXTRA_USER, user)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                return
            }
        }

        setupInputValidation()
        setupListeners()
    }

    private fun setupInputValidation() {
        // Limpiar errores en tiempo real cuando el usuario escribe
        binding.etUser.doAfterTextChanged {
            if (!it.isNullOrBlank()) {
                binding.tilUser.error = null
            }
        }

        binding.etPassword.doAfterTextChanged {
            if (!it.isNullOrBlank()) {
                binding.tilPassword.error = null
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }
    }

    private fun attemptLogin() {
        val user = binding.etUser.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        var hasError = false

        // CA1: Validar campos vacíos con mensaje de error debajo de cada campo
        if (user.isEmpty()) {
            binding.tilUser.error = getString(R.string.error_empty_user)
            hasError = true
        } else {
            binding.tilUser.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            hasError = true
        } else {
            binding.tilPassword.error = null
        }

        if (hasError) return

        // Iniciar proceso de autenticación
        setLoading(true)

        lifecycleScope.launch {
            val result = authRepository.login(user, password)
            setLoading(false)

            result.onSuccess { usuario ->
                onLoginSuccess(usuario)
            }.onFailure { exception ->
                onLoginFailure(exception.message)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etUser.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun onLoginSuccess(usuario: Usuario) {
        // Guardar sesión
        sessionManager.saveUser(usuario)

        Toast.makeText(
            this,
            getString(R.string.login_success, usuario.usuario),
            Toast.LENGTH_SHORT
        ).show()

        // CA2: Navegar al menú principal y limpiar LoginActivity de la pila de navegación
        val intent = Intent(this, MenuActivity::class.java).apply {
            putExtra(MenuActivity.EXTRA_USER, usuario)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun onLoginFailure(errorMessage: String?) {
        // CA3: Mostrar mensaje de credenciales incorrectas
        val msg = if (errorMessage?.contains("Credenciales incorrectas", ignoreCase = true) == true) {
            getString(R.string.error_invalid_credentials)
        } else {
            errorMessage ?: getString(R.string.error_invalid_credentials)
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}

