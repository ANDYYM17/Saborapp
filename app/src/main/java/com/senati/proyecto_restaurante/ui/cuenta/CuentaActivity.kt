package com.senati.proyecto_restaurante.ui.cuenta

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Pedido
import com.senati.proyecto_restaurante.data.repository.PedidoRepository
import com.senati.proyecto_restaurante.databinding.ActivityCuentaBinding
import com.senati.proyecto_restaurante.ui.pedido.DetallePedidoAdapter
import kotlinx.coroutines.launch
import java.util.Locale

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private val pedidoRepository = PedidoRepository()
    private val detalleAdapter = DetallePedidoAdapter()

    private var mesaId: Int = -1
    private var mesaNumero: Int = -1
    private var pedidoActual: Pedido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaId = intent.getIntExtra("EXTRA_MESA_ID", -1)
        mesaNumero = intent.getIntExtra("EXTRA_MESA_NUMERO", -1)

        setupUI()
        cargarCuenta()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.tvMesaNumeroTitle.text = getString(R.string.cuenta_mesa_title, mesaNumero)

        binding.rvCuentaDetalle.apply {
            layoutManager = LinearLayoutManager(this@CuentaActivity)
            adapter = detalleAdapter
        }

        binding.btnCerrarCuenta.setOnClickListener {
            mostrarDialogConfirmacionCierre()
        }

        binding.btnCompartirCuenta.setOnClickListener {
            compartirPorWhatsApp()
        }
    }

    private fun cargarCuenta() {
        if (mesaId == -1) {
            Toast.makeText(this, "Mesa no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = pedidoRepository.obtenerPedidoPorMesa(mesaId)
            binding.progressBar.visibility = View.GONE
            result.onSuccess { pedido ->
                pedidoActual = pedido
                if (pedido != null) {
                    binding.tvFechaPedido.text = "Fecha: ${pedido.fecha}"
                    detalleAdapter.updateData(pedido.detalles)
                    binding.tvTotalCuenta.text = String.format(Locale.getDefault(), "S/ %.2f", pedido.total)
                } else {
                    Toast.makeText(this@CuentaActivity, getString(R.string.msg_no_pedido_activo), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.onFailure { error ->
                Toast.makeText(this@CuentaActivity, error.message ?: "Error al cargar cuenta", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun mostrarDialogConfirmacionCierre() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.title_confirmar_cierre)
            .setMessage(getString(R.string.msg_confirmar_cierre, mesaNumero))
            .setPositiveButton(R.string.btn_confirmar) { _, _ ->
                ejecutarCierreCuenta()
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }

    private fun ejecutarCierreCuenta() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = pedidoRepository.cerrarCuenta(mesaId)
            binding.progressBar.visibility = View.GONE
            result.onSuccess { msg ->
                MaterialAlertDialogBuilder(this@CuentaActivity)
                    .setTitle(R.string.title_cuenta_cerrada)
                    .setMessage(msg)
                    .setPositiveButton(R.string.btn_aceptar) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            }.onFailure { error ->
                Toast.makeText(this@CuentaActivity, error.message ?: "Error al cerrar cuenta", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun compartirPorWhatsApp() {
        val pedido = pedidoActual
        if (pedido == null || pedido.detalles.isEmpty()) {
            Toast.makeText(this, "No hay consumos para compartir", Toast.LENGTH_SHORT).show()
            return
        }

        val sb = StringBuilder()
        sb.append("🍽️ *SABORAPP - RESUMEN DE CUENTA* 🍽️\n")
        sb.append("------------------------------------\n")
        sb.append("📌 *Mesa:* Mesa ").append(mesaNumero).append("\n")
        sb.append("📅 *Fecha:* ").append(pedido.fecha).append("\n")
        sb.append("------------------------------------\n")
        sb.append("*DETALLE DE CONSUMO:*\n")

        for (item in pedido.detalles) {
            val nombre = item.nombrePlato ?: "Item"
            sb.append(String.format(Locale.getDefault(), "• %dx %s (S/ %.2f) = S/ %.2f\n",
                item.cantidad, nombre, item.precioUnit, item.subtotal))
        }

        sb.append("------------------------------------\n")
        sb.append(String.format(Locale.getDefault(), "💰 *TOTAL A PAGAR:* S/ %.2f\n", pedido.total))
        sb.append("------------------------------------\n")
        sb.append("¡Gracias por su preferencia!\n")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.btn_compartir_whatsapp))
        try {
            startActivity(shareIntent)
        } catch (_: Exception) {
            Toast.makeText(this, "No se encontró aplicación para compartir", Toast.LENGTH_SHORT).show()
        }
    }
}
