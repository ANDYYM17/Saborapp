package com.senati.proyecto_restaurante.ui.pedido

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Mesa
import com.senati.proyecto_restaurante.data.model.Pedido
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.data.repository.MesaRepository
import com.senati.proyecto_restaurante.data.repository.PedidoRepository
import com.senati.proyecto_restaurante.data.repository.PlatoRepository
import com.senati.proyecto_restaurante.databinding.ActivityPedidoBinding
import com.senati.proyecto_restaurante.databinding.DialogAgregarItemPedidoBinding
import com.senati.proyecto_restaurante.ui.cuenta.CuentaActivity
import kotlinx.coroutines.launch
import java.util.Locale

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private val mesaRepository = MesaRepository()
    private val platoRepository = PlatoRepository()
    private val pedidoRepository = PedidoRepository()

    private val detalleAdapter = DetallePedidoAdapter()
    private var listaMesas: List<Mesa> = emptyList()
    private var mesaSeleccionada: Mesa? = null
    private var pedidoActual: Pedido? = null
    private var platosDisponibles: List<Plato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        cargarMesas()
    }

    override fun onResume() {
        super.onResume()
        mesaSeleccionada?.let {
            cargarPedidoMesa(it.id)
        }
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.rvDetallePedido.apply {
            layoutManager = LinearLayoutManager(this@PedidoActivity)
            adapter = detalleAdapter
        }

        binding.btnAgregarPlato.setOnClickListener {
            if (mesaSeleccionada == null) {
                Toast.makeText(this, "Seleccione una mesa primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mostrarDialogAgregarPlato()
        }

        binding.btnVerCuenta.setOnClickListener {
            val mesa = mesaSeleccionada
            if (mesa == null) {
                Toast.makeText(this, "Seleccione una mesa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pedidoActual == null || pedidoActual?.detalles.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.msg_no_pedido_activo), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CuentaActivity::class.java).apply {
                putExtra("EXTRA_MESA_ID", mesa.id)
                putExtra("EXTRA_MESA_NUMERO", mesa.numero)
            }
            startActivity(intent)
        }
    }

    private fun cargarMesas() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = mesaRepository.listarMesas()
            binding.progressBar.visibility = View.GONE
            result.onSuccess { mesas ->
                listaMesas = mesas
                if (mesas.isEmpty()) {
                    Toast.makeText(this@PedidoActivity, "No hay mesas registradas", Toast.LENGTH_LONG).show()
                    return@onSuccess
                }

                val spinnerItems = mesas.map { "Mesa ${it.numero} (${it.estado})" }
                val adapter = ArrayAdapter(this@PedidoActivity, android.R.layout.simple_spinner_dropdown_item, spinnerItems)
                binding.spinnerMesas.adapter = adapter

                val targetMesaId = intent.getIntExtra("EXTRA_MESA_ID", -1)
                val initialIndex = if (targetMesaId != -1) {
                    mesas.indexOfFirst { it.id == targetMesaId }.takeIf { it >= 0 } ?: 0
                } else 0

                binding.spinnerMesas.setSelection(initialIndex)

                binding.spinnerMesas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        mesaSeleccionada = listaMesas[position]
                        actualizarBadgeEstado(mesaSeleccionada!!)
                        cargarPedidoMesa(mesaSeleccionada!!.id)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }.onFailure { error ->
                Toast.makeText(this@PedidoActivity, error.message ?: "Error al cargar mesas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarBadgeEstado(mesa: Mesa) {
        binding.tvMesaEstadoBadge.text = mesa.estado
        if (mesa.estado == "LIBRE") {
            binding.tvMesaEstadoBadge.setTextColor(ContextCompat.getColor(this, R.color.status_libre))
            binding.tvMesaEstadoBadge.setBackgroundResource(R.drawable.bg_status_libre)
        } else {
            binding.tvMesaEstadoBadge.setTextColor(ContextCompat.getColor(this, R.color.status_ocupada))
            binding.tvMesaEstadoBadge.setBackgroundResource(R.drawable.bg_status_ocupada)
        }
    }

    private fun cargarPedidoMesa(idMesa: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = pedidoRepository.obtenerPedidoPorMesa(idMesa)
            binding.progressBar.visibility = View.GONE
            result.onSuccess { pedido ->
                pedidoActual = pedido
                if (pedido != null && pedido.detalles.isNotEmpty()) {
                    binding.llEmptyPedido.visibility = View.GONE
                    binding.rvDetallePedido.visibility = View.VISIBLE
                    detalleAdapter.updateData(pedido.detalles)
                    binding.tvTotalPedido.text = String.format(Locale.getDefault(), "S/ %.2f", pedido.total)
                } else {
                    binding.llEmptyPedido.visibility = View.VISIBLE
                    binding.rvDetallePedido.visibility = View.GONE
                    detalleAdapter.updateData(emptyList())
                    binding.tvTotalPedido.text = "S/ 0.00"
                }
            }.onFailure {
                binding.llEmptyPedido.visibility = View.VISIBLE
                binding.rvDetallePedido.visibility = View.GONE
                detalleAdapter.updateData(emptyList())
                binding.tvTotalPedido.text = "S/ 0.00"
            }
        }
    }

    private fun mostrarDialogAgregarPlato() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = platoRepository.listarPlatos(soloDisponibles = true)
            binding.progressBar.visibility = View.GONE
            result.onSuccess { platos ->
                if (platos.isEmpty()) {
                    Toast.makeText(this@PedidoActivity, "No hay platos disponibles", Toast.LENGTH_SHORT).show()
                    return@onSuccess
                }
                platosDisponibles = platos
                abrirDialogSeleccion(platos)
            }.onFailure { error ->
                Toast.makeText(this@PedidoActivity, error.message ?: "Error al cargar platos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirDialogSeleccion(platos: List<Plato>) {
        val dialogBinding = DialogAgregarItemPedidoBinding.inflate(LayoutInflater.from(this))
        val platoNames = platos.map { "${it.nombre} - S/ ${String.format(Locale.getDefault(), "%.2f", it.precio)}" }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, platoNames)
        dialogBinding.spinnerPlatosDisponibles.adapter = spinnerAdapter

        fun actualizarPreview() {
            val selectedIdx = dialogBinding.spinnerPlatosDisponibles.selectedItemPosition
            val cantStr = dialogBinding.etCantidad.text.toString().trim()
            val cant = cantStr.toIntOrNull() ?: 0
            if (selectedIdx in platos.indices && cant > 0) {
                val plato = platos[selectedIdx]
                val subtotal = plato.precio * cant
                dialogBinding.tvSubtotalPreview.text = String.format(Locale.getDefault(), "Subtotal: S/ %.2f", subtotal)
            } else {
                dialogBinding.tvSubtotalPreview.text = "Subtotal: S/ 0.00"
            }
        }

        dialogBinding.spinnerPlatosDisponibles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualizarPreview()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        dialogBinding.etCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                actualizarPreview()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        actualizarPreview()

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_agregar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialog.setOnShowListener {
            val btnPos = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            btnPos.setOnClickListener {
                val selectedIdx = dialogBinding.spinnerPlatosDisponibles.selectedItemPosition
                val cantStr = dialogBinding.etCantidad.text.toString().trim()
                val cant = cantStr.toIntOrNull()

                if (cant == null || cant <= 0) {
                    dialogBinding.tilCantidad.error = getString(R.string.error_cantidad_invalida)
                    return@setOnClickListener
                }
                dialogBinding.tilCantidad.error = null

                if (selectedIdx !in platos.indices) {
                    Toast.makeText(this, "Seleccione un plato válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val platoSeleccionado = platos[selectedIdx]
                val mesa = mesaSeleccionada ?: return@setOnClickListener

                agregarPlatoAPedido(mesa.id, platoSeleccionado.id, cant, dialog)
            }
        }

        dialog.show()
    }

    private fun agregarPlatoAPedido(idMesa: Int, idPlato: Int, cantidad: Int, dialog: androidx.appcompat.app.AlertDialog) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = pedidoRepository.agregarItem(idMesa, idPlato, cantidad)
            binding.progressBar.visibility = View.GONE
            result.onSuccess { msg ->
                dialog.dismiss()
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                mesaSeleccionada?.let {
                    it.estado = "OCUPADA"
                    actualizarBadgeEstado(it)
                    cargarPedidoMesa(it.id)
                }
            }.onFailure { error ->
                Toast.makeText(this@PedidoActivity, error.message ?: "Error al agregar plato", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
