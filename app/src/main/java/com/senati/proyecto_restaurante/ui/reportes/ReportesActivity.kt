package com.senati.proyecto_restaurante.ui.reportes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.ReporteResumen
import com.senati.proyecto_restaurante.data.model.TopPlato
import com.senati.proyecto_restaurante.data.model.VentaMesa
import com.senati.proyecto_restaurante.data.repository.ReporteRepository
import com.senati.proyecto_restaurante.databinding.ActivityReportesBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private val reporteRepository = ReporteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        cargarReportes()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvFechaHoy.text = sdf.format(Date())
    }

    private fun cargarReportes() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = reporteRepository.obtenerReportes()
            binding.progressBar.visibility = View.GONE

            result.onSuccess { resumen ->
                renderizarReportes(resumen)
            }.onFailure { error ->
                Toast.makeText(this@ReportesActivity, error.message ?: "Error al cargar reportes", Toast.LENGTH_SHORT).show()
                binding.tvSinVentasAviso.visibility = View.VISIBLE
            }
        }
    }

    private fun renderizarReportes(resumen: ReporteResumen) {
        // 1. Venta del Día (HU-10)
        binding.tvTotalVentaDia.text = String.format(Locale.getDefault(), "S/ %.2f", resumen.ventaTotalHoy)
        binding.tvCantidadPedidosDia.text = String.format(Locale.getDefault(), "%d pedidos cerrados hoy", resumen.cantidadPedidosHoy)

        if (!resumen.tieneVentasHoy && resumen.ventaTotalHoy <= 0.0) {
            binding.tvSinVentasAviso.visibility = View.VISIBLE
        } else {
            binding.tvSinVentasAviso.visibility = View.GONE
        }

        // 2. Top 5 Platos Más Vendidos (HU-10)
        binding.containerTopPlatos.removeAllViews()
        if (resumen.topPlatos.isEmpty()) {
            binding.tvEmptyTopPlatos.visibility = View.VISIBLE
        } else {
            binding.tvEmptyTopPlatos.visibility = View.GONE
            resumen.topPlatos.forEachIndexed { index, plato ->
                val view = crearFilaTopPlato(index + 1, plato)
                binding.containerTopPlatos.addView(view)
            }
        }

        // 3. Ventas por Mesa (HU-10)
        binding.containerVentasMesa.removeAllViews()
        if (resumen.ventasPorMesa.isEmpty()) {
            binding.tvEmptyVentasMesa.visibility = View.VISIBLE
        } else {
            binding.tvEmptyVentasMesa.visibility = View.GONE
            for (mesa in resumen.ventasPorMesa) {
                val view = crearFilaVentaMesa(mesa)
                binding.containerVentasMesa.addView(view)
            }
        }
    }

    private fun crearFilaTopPlato(posicion: Int, plato: TopPlato): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val tvBadge = TextView(this).apply {
            text = "#$posicion"
            textSize = 14f
            setTextColor(getColor(R.color.primary))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 12, 0)
        }

        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvNombre = TextView(this).apply {
            text = plato.nombrePlato
            textSize = 14f
            setTextColor(getColor(R.color.text_primary))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val tvDetalle = TextView(this).apply {
            text = String.format(Locale.getDefault(), "%s • %d pedidos", plato.categoria, plato.totalVendido)
            textSize = 12f
            setTextColor(getColor(R.color.text_secondary))
        }

        infoLayout.addView(tvNombre)
        infoLayout.addView(tvDetalle)

        val tvMonto = TextView(this).apply {
            text = String.format(Locale.getDefault(), "S/ %.2f", plato.totalMonto)
            textSize = 14f
            setTextColor(getColor(R.color.primary))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        row.addView(tvBadge)
        row.addView(infoLayout)
        row.addView(tvMonto)

        return row
    }

    private fun crearFilaVentaMesa(mesa: VentaMesa): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvMesa = TextView(this).apply {
            text = String.format(Locale.getDefault(), "Mesa %d", mesa.numeroMesa)
            textSize = 14f
            setTextColor(getColor(R.color.text_primary))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val tvPedidos = TextView(this).apply {
            text = String.format(Locale.getDefault(), "%d pedidos completados", mesa.totalPedidos)
            textSize = 12f
            setTextColor(getColor(R.color.text_secondary))
        }

        infoLayout.addView(tvMesa)
        infoLayout.addView(tvPedidos)

        val tvTotal = TextView(this).apply {
            text = String.format(Locale.getDefault(), "S/ %.2f", mesa.totalMesa)
            textSize = 14f
            setTextColor(getColor(R.color.primary))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        row.addView(infoLayout)
        row.addView(tvTotal)

        return row
    }
}
