package com.senati.proyecto_restaurante.ui.platos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.senati.proyecto_restaurante.databinding.ActivityPlatosBinding

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

