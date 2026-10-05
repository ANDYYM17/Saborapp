package com.senati.proyecto_restaurante.ui.platos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.data.repository.PlatoRepository
import com.senati.proyecto_restaurante.databinding.ActivityFormularioPlatoBinding
import kotlinx.coroutines.launch

class FormularioPlatoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioPlatoBinding
    private val platoRepository = PlatoRepository()

    private val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioPlatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinner()
        setupValidationListeners()
        setupSaveButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        // CA3: Spinner con Entradas, Fondos, Bebidas, Postres
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )
        binding.spinnerCategoria.adapter = adapter
        binding.spinnerCategoria.setSelection(1) // Seleccionar "Fondos" por defecto
    }

    private fun setupValidationListeners() {
        binding.etNombre.doAfterTextChanged {
            if (!it.isNullOrBlank()) {
                binding.tilNombre.error = null
            }
        }

        binding.etPrecio.doAfterTextChanged {
            if (!it.isNullOrBlank()) {
                binding.tilPrecio.error = null
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnGuardarPlato.setOnClickListener {
            attemptSavePlato()
        }
    }

    private fun attemptSavePlato() {
        val nombre = binding.etNombre.text?.toString()?.trim().orEmpty()
        val precioStr = binding.etPrecio.text?.toString()?.trim().orEmpty()
        val categoria = binding.spinnerCategoria.selectedItem?.toString() ?: "Fondos"
        val disponible = if (binding.switchDisponible.isChecked) 1 else 0

        var hasError = false

        // CA1: Validación de nombre vacío
        if (nombre.isEmpty()) {
            binding.tilNombre.error = getString(R.string.error_empty_nombre)
            hasError = true
        } else {
            binding.tilNombre.error = null
        }

        // CA1 & CA2: Validación de precio vacío y precio <= 0
        if (precioStr.isEmpty()) {
            binding.tilPrecio.error = getString(R.string.error_empty_precio)
            hasError = true
        } else {
            val precio = precioStr.toDoubleOrNull()
            if (precio == null || precio <= 0.0) {
                binding.tilPrecio.error = getString(R.string.error_invalid_precio)
                hasError = true
            } else {
                binding.tilPrecio.error = null
            }
        }

        if (hasError) return

        val precioVal = precioStr.toDouble()
        val nuevoPlato = Plato(
            nombre = nombre,
            categoria = categoria,
            precio = precioVal,
            disponible = disponible
        )

        setLoading(true)

        // CA4: Persistencia en MySQL
        lifecycleScope.launch {
            val result = platoRepository.registrarPlato(nuevoPlato)
            setLoading(false)

            result.onSuccess {
                Toast.makeText(
                    this@FormularioPlatoActivity,
                    getString(R.string.msg_plato_guardado),
                    Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_OK)
                finish()
            }.onFailure { exception ->
                Toast.makeText(
                    this@FormularioPlatoActivity,
                    exception.message ?: getString(R.string.error_server_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnGuardarPlato.isEnabled = !isLoading
        binding.etNombre.isEnabled = !isLoading
        binding.etPrecio.isEnabled = !isLoading
        binding.spinnerCategoria.isEnabled = !isLoading
        binding.switchDisponible.isEnabled = !isLoading
    }
}

