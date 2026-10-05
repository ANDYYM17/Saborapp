package com.senati.proyecto_restaurante.ui.platos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.data.repository.PlatoRepository
import com.senati.proyecto_restaurante.databinding.ActivityPlatosBinding
import kotlinx.coroutines.launch

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    private val platoRepository = PlatoRepository()
    private lateinit var adapter: PlatosAdapter

    private val formPlatoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            cargarPlatos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupCategoryFilters()
        setupSearchInput()
        setupListeners()
        cargarPlatos()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // HU-07: Al hacer click en un plato, abrir formulario en modo edición
        adapter = PlatosAdapter { plato ->
            abrirFormularioEdicion(plato)
        }
        binding.rvPlatos.layoutManager = LinearLayoutManager(this)
        binding.rvPlatos.adapter = adapter
    }

    private fun abrirFormularioEdicion(plato: Plato) {
        val intent = Intent(this, FormularioPlatoActivity::class.java).apply {
            putExtra(FormularioPlatoActivity.EXTRA_PLATO, plato)
        }
        formPlatoLauncher.launch(intent)
    }

    private fun setupCategoryFilters() {
        binding.chipGroupCategorias.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val categoria = when (checkedIds.first()) {
                R.id.chipEntradas -> "Entradas"
                R.id.chipFondos -> "Fondos"
                R.id.chipBebidas -> "Bebidas"
                R.id.chipPostres -> "Postres"
                else -> "TODOS"
            }

            adapter.filtrarPorCategoria(categoria)
            actualizarEstadoVacio()
        }
    }

    private fun setupSearchInput() {
        // HU-07: Búsqueda en tiempo real mientras el usuario escribe
        binding.etBuscarPlato.doAfterTextChanged { text ->
            adapter.filtrarPorTexto(text?.toString().orEmpty())
            actualizarEstadoVacio()
        }
    }

    private fun setupListeners() {
        binding.fabNuevoPlato.setOnClickListener {
            val intent = Intent(this, FormularioPlatoActivity::class.java)
            formPlatoLauncher.launch(intent)
        }
    }

    private fun cargarPlatos() {
        setLoading(true)

        lifecycleScope.launch {
            val result = platoRepository.listarPlatos()
            setLoading(false)

            result.onSuccess { platos ->
                adapter.updateData(platos)
                actualizarEstadoVacio()
            }.onFailure { exception ->
                Toast.makeText(
                    this@PlatosActivity,
                    exception.message ?: getString(R.string.error_server_connection),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun actualizarEstadoVacio() {
        if (adapter.getFilteredItemCount() == 0) {
            binding.llEmptyState.visibility = View.VISIBLE
            binding.rvPlatos.visibility = View.GONE
        } else {
            binding.llEmptyState.visibility = View.GONE
            binding.rvPlatos.visibility = View.VISIBLE
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
