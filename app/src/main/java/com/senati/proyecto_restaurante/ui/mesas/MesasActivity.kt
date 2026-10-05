package com.senati.proyecto_restaurante.ui.mesas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Mesa
import com.senati.proyecto_restaurante.data.repository.MesaRepository
import com.senati.proyecto_restaurante.databinding.ActivityMesasBinding
import com.senati.proyecto_restaurante.databinding.DialogRegistrarMesaBinding
import kotlinx.coroutines.launch

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private val mesaRepository = MesaRepository()
    private lateinit var adapter: MesasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        cargarMesas()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = MesasAdapter { mesa ->
            val intent = android.content.Intent(this, com.senati.proyecto_restaurante.ui.pedido.PedidoActivity::class.java).apply {
                putExtra("EXTRA_MESA_ID", mesa.id)
            }
            startActivity(intent)
        }
        // HU-06: GridLayoutManager con 3 columnas
        binding.rvMesas.layoutManager = GridLayoutManager(this, 3)
        binding.rvMesas.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabNuevaMesa.setOnClickListener {
            mostrarDialogNuevaMesa()
        }
    }

    private fun cargarMesas() {
        setLoading(true)

        lifecycleScope.launch {
            val result = mesaRepository.listarMesas()
            setLoading(false)

            result.onSuccess { mesas ->
                if (mesas.isEmpty()) {
                    binding.llEmptyState.visibility = View.VISIBLE
                    binding.rvMesas.visibility = View.GONE
                } else {
                    binding.llEmptyState.visibility = View.GONE
                    binding.rvMesas.visibility = View.VISIBLE
                    adapter.updateData(mesas)
                }
            }.onFailure { exception ->
                Toast.makeText(
                    this@MesasActivity,
                    exception.message ?: getString(R.string.error_server_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarDialogNuevaMesa() {
        val dialogBinding = DialogRegistrarMesaBinding.inflate(LayoutInflater.from(this))

        dialogBinding.etNumeroMesa.doAfterTextChanged {
            if (!it.isNullOrBlank()) dialogBinding.tilNumeroMesa.error = null
        }

        dialogBinding.etCapacidadMesa.doAfterTextChanged {
            if (!it.isNullOrBlank()) dialogBinding.tilCapacidadMesa.error = null
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.btn_guardar_mesa), null)
            .setNegativeButton(getString(R.string.btn_cancelar)) { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val numStr = dialogBinding.etNumeroMesa.text?.toString()?.trim().orEmpty()
                val capStr = dialogBinding.etCapacidadMesa.text?.toString()?.trim().orEmpty()

                var hasError = false

                // Validación de número vacío o <= 0
                if (numStr.isEmpty()) {
                    dialogBinding.tilNumeroMesa.error = getString(R.string.error_empty_numero_mesa)
                    hasError = true
                } else {
                    val num = numStr.toIntOrNull()
                    if (num == null || num <= 0) {
                        dialogBinding.tilNumeroMesa.error = "Número de mesa inválido"
                        hasError = true
                    } else {
                        dialogBinding.tilNumeroMesa.error = null
                    }
                }

                // CA2: Capacidad válida entre 1 y 12
                if (capStr.isEmpty()) {
                    dialogBinding.tilCapacidadMesa.error = getString(R.string.error_empty_capacidad_mesa)
                    hasError = true
                } else {
                    val cap = capStr.toIntOrNull()
                    if (cap == null || cap < 1 || cap > 12) {
                        dialogBinding.tilCapacidadMesa.error = getString(R.string.error_capacidad_invalida)
                        hasError = true
                    } else {
                        dialogBinding.tilCapacidadMesa.error = null
                    }
                }

                if (hasError) return@setOnClickListener

                val numero = numStr.toInt()
                val capacidad = capStr.toInt()

                // CA3: Estado inicial LIBRE
                val nuevaMesa = Mesa(
                    numero = numero,
                    capacidad = capacidad,
                    estado = "LIBRE"
                )

                saveButton.isEnabled = false

                lifecycleScope.launch {
                    val result = mesaRepository.registrarMesa(nuevaMesa)
                    saveButton.isEnabled = true

                    result.onSuccess {
                        Toast.makeText(
                            this@MesasActivity,
                            getString(R.string.msg_mesa_guardada),
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        cargarMesas() // Recargar inmediatamente la lista
                    }.onFailure { exception ->
                        val errorMsg = exception.message ?: ""
                        if (errorMsg.contains("ya existe", ignoreCase = true)) {
                            // CA1: Mesa duplicada
                            dialogBinding.tilNumeroMesa.error = getString(R.string.error_mesa_duplicada)
                        } else if (errorMsg.contains("Capacidad inválida", ignoreCase = true)) {
                            dialogBinding.tilCapacidadMesa.error = getString(R.string.error_capacidad_invalida)
                        } else {
                            Toast.makeText(this@MesasActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

