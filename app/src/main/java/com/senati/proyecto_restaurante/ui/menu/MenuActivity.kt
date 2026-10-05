package com.senati.proyecto_restaurante.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Usuario
import com.senati.proyecto_restaurante.databinding.ActivityMenuBinding
import com.senati.proyecto_restaurante.ui.login.LoginActivity
import com.senati.proyecto_restaurante.ui.mesas.MesasActivity
import com.senati.proyecto_restaurante.ui.pedido.PedidoActivity
import com.senati.proyecto_restaurante.ui.platos.PlatosActivity
import com.senati.proyecto_restaurante.ui.reportes.ReportesActivity
import com.senati.proyecto_restaurante.utils.SessionManager

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var sessionManager: SessionManager
    private var currentUser: Usuario? = null

    companion object {
        const val EXTRA_USER = "extra_current_user"
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_MOZO = "MOZO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        loadUserData()
        setupRoleAccess()
        setupListeners()
    }

    private fun loadUserData() {
        // Obtener usuario desde el intent o desde el SessionManager
        @Suppress("DEPRECATION")
        currentUser = intent.getSerializableExtra(EXTRA_USER) as? Usuario ?: sessionManager.getUser()

        currentUser?.let { user ->
            binding.tvUserGreeting.text = getString(R.string.menu_greeting, user.usuario)
            binding.tvRoleBadge.text = user.rol.uppercase()

            if (user.rol.equals(ROLE_ADMIN, ignoreCase = true)) {
                binding.tvRoleBadge.setBackgroundResource(R.drawable.bg_role_admin)
                binding.tvRoleBadge.setTextColor(ContextCompat.getColor(this, R.color.role_admin_text))
            } else {
                binding.tvRoleBadge.setBackgroundResource(R.drawable.bg_role_mozo)
                binding.tvRoleBadge.setTextColor(ContextCompat.getColor(this, R.color.role_mozo_text))
            }
        }
    }

    private fun setupRoleAccess() {
        val role = currentUser?.rol?.uppercase() ?: ROLE_MOZO

        // CA4: Si el usuario es MOZO, Reportes NO debe mostrarse. Si es ADMIN, sí se muestra.
        if (role == ROLE_ADMIN) {
            binding.cardReportes.visibility = View.VISIBLE
        } else {
            binding.cardReportes.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        // CA2: Navegación a cada pantalla correspondiente
        binding.cardPlatos.setOnClickListener {
            startActivity(Intent(this, PlatosActivity::class.java))
        }

        binding.cardMesas.setOnClickListener {
            startActivity(Intent(this, MesasActivity::class.java))
        }

        binding.cardPedidos.setOnClickListener {
            startActivity(Intent(this, PedidoActivity::class.java))
        }

        binding.cardReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        // CA3: Salir debe regresar al Login limpiando la sesión
        binding.cardSalir.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

