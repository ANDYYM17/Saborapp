package com.senati.proyecto_restaurante.ui.pedido

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.senati.proyecto_restaurante.databinding.ActivityPedidoBinding

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

