package com.senati.proyecto_restaurante.ui.mesas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.senati.proyecto_restaurante.databinding.ActivityMesasBinding

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

