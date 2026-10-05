package com.senati.proyecto_restaurante.ui.platos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.data.repository.PlatoRepository
import com.senati.proyecto_restaurante.databinding.ActivityFormularioPlatoBinding
import kotlinx.coroutines.launch
import java.util.Locale

class FormularioPlatoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioPlatoBinding
    private val platoRepository = PlatoRepository()

    private val categorias = arrayOf("Entradas", "Fondos", "Bebidas", "Postres")
    private var platoAEditar: Plato? = null
    private var esModoEdicion: Boolean = false

    companion object {
        const val EXTRA_PLATO = "extra_plato_a_editar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioPlatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        platoAEditar = intent.getSerializableExtra(EXTRA_PLATO) as? Plato
        esModoEdicion = platoAEditar != null

        setupToolbar()
        setupSpinner()
        setupValidationListeners()
        setupFormMode()
        setupActionButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.title = if (esModoEdicion) getString(R.string.title_editar_plato) else getString(R.string.title_registrar_plato)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )
        binding.spinnerCategoria.adapter = adapter
    }

    private fun setupFormMode() {
        if (esModoEdicion && platoAEditar != null) {
            val plato = platoAEditar!!
            binding.tvFormTitle.text = getString(R.string.title_editar_plato)
            binding.tvFormSubtitle.text = getString(R.string.subtitle_editar_plato)
            binding.btnGuardarPlato.text = getString(R.string.btn_actualizar_plato)
            binding.btnEliminarPlato.visibility = View.VISIBLE

            binding.etNombre.setText(plato.nombre)
            binding.etPrecio.setText(String.format(Locale.US, "%.2f", plato.precio))
            binding.switchDisponible.isChecked = plato.disponible == 1

            val pos = categorias.indexOfFirst { it.equals(plato.categoria, ignoreCase = true) }
            if (pos >= 0) {
                binding.spinnerCategoria.setSelection(pos)
            }
        } else {
            binding.tvFormTitle.text = getString(R.string.title_registrar_plato)
            binding.tvFormSubtitle.text = getString(R.string.subtitle_registrar_plato)
            binding.btnGuardarPlato.text = getString(R.string.btn_guardar_plato)
            binding.btnEliminarPlato.visibility = View.GONE
            binding.spinnerCategoria.setSelection(1) // "Fondos" por defecto
        }
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

    private fun setupActionButtons() {
        binding.btnGuardarPlato.setOnClickListener {
            attemptSaveOrUpdate()
        }

        binding.btnEliminarPlato.setOnClickListener {
            confirmarEliminarPlato()
        }
    }

    private fun attemptSaveOrUpdate() {
        val nombre = binding.etNombre.text?.toString()?.trim().orEmpty()
        val precioStr = binding.etPrecio.text?.toString()?.trim().orEmpty()
        val categoria = binding.spinnerCategoria.selectedItem?.toString() ?: "Fondos"
        val disponible = if (binding.switchDisponible.isChecked) 1 else 0

        var hasError = false

        if (nombre.isEmpty()) {
            binding.tilNombre.error = getString(R.string.error_empty_nombre)
            hasError = true
        } else {
            binding.tilNombre.error = null
        }

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
        val platoPayload = Plato(
            id = platoAEditar?.id ?: 0,
            nombre = nombre,
            categoria = categoria,
            precio = precioVal,
            disponible = disponible
        )

        setLoading(true)

        lifecycleScope.launch {
            val result = if (esModoEdicion) {
                platoRepository.actualizarPlato(platoPayload)
            } else {
                platoRepository.registrarPlato(platoPayload)
            }
            setLoading(false)

            result.onSuccess {
                val msg = if (esModoEdicion) getString(R.string.msg_plato_actualizado) else getString(R.string.msg_plato_guardado)
                Toast.makeText(this@FormularioPlatoActivity, msg, Toast.LENGTH_SHORT).show()
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

    private fun confirmarEliminarPlato() {
        val plato = platoAEditar ?: return

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_eliminar_plato_title))
            .setMessage(getString(R.string.dialog_eliminar_plato_msg))
            .setPositiveButton(getString(R.string.btn_confirmar)) { _, _ ->
                ejecutarEliminarPlato(plato.id)
            }
            .setNegativeButton(getString(R.string.btn_cancelar), null)
            .show()
    }

    private fun ejecutarEliminarPlato(idPlato: Int) {
        setLoading(true)

        lifecycleScope.launch {
            val result = platoRepository.eliminarPlato(idPlato)
            setLoading(false)

            result.onSuccess {
                Toast.makeText(
                    this@FormularioPlatoActivity,
                    getString(R.string.msg_plato_eliminado),
                    Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_OK)
                finish()
            }.onFailure { exception ->
                val errorMsg = exception.message ?: ""
                if (errorMsg.contains("tiene pedidos", ignoreCase = true)) {
                    // CA2: Restricción de pedidos asociados
                    Toast.makeText(
                        this@FormularioPlatoActivity,
                        getString(R.string.error_no_se_puede_eliminar_pedidos),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this@FormularioPlatoActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnGuardarPlato.isEnabled = !isLoading
        binding.btnEliminarPlato.isEnabled = !isLoading
        binding.etNombre.isEnabled = !isLoading
        binding.etPrecio.isEnabled = !isLoading
        binding.spinnerCategoria.isEnabled = !isLoading
        binding.switchDisponible.isEnabled = !isLoading
    }
}
