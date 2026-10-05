package com.senati.proyecto_restaurante.ui.platos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Plato
import com.senati.proyecto_restaurante.databinding.ItemPlatoBinding
import java.util.Locale

class PlatosAdapter(
    private var listaOriginal: List<Plato> = emptyList(),
    private val onItemClick: ((Plato) -> Unit)? = null
) : RecyclerView.Adapter<PlatosAdapter.PlatoViewHolder>() {

    private var listaFiltrada: List<Plato> = emptyList()
    private var categoriaActual: String = "TODOS"
    private var queryBusqueda: String = ""

    fun updateData(newPlatos: List<Plato>) {
        this.listaOriginal = newPlatos
        aplicarFiltros()
    }

    fun filtrarPorCategoria(categoria: String) {
        this.categoriaActual = categoria.uppercase(Locale.getDefault())
        aplicarFiltros()
    }

    fun filtrarPorTexto(query: String) {
        this.queryBusqueda = query.trim().lowercase(Locale.getDefault())
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        listaFiltrada = listaOriginal.filter { plato ->
            val matchCategoria = if (categoriaActual == "TODOS" || categoriaActual.isEmpty()) {
                true
            } else {
                plato.categoria.equals(categoriaActual, ignoreCase = true)
            }

            val matchTexto = if (queryBusqueda.isEmpty()) {
                true
            } else {
                plato.nombre.lowercase(Locale.getDefault()).contains(queryBusqueda) ||
                        plato.categoria.lowercase(Locale.getDefault()).contains(queryBusqueda)
            }

            matchCategoria && matchTexto
        }
        notifyDataSetChanged()
    }

    fun getFilteredItemCount(): Int = listaFiltrada.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        holder.bind(listaFiltrada[position])
    }

    override fun getItemCount(): Int = listaFiltrada.size

    inner class PlatoViewHolder(private val binding: ItemPlatoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plato: Plato) {
            val context = itemView.context

            binding.tvNombrePlato.text = plato.nombre
            binding.tvCategoriaPlato.text = plato.categoria
            binding.tvPrecioPlato.text = String.format(Locale.getDefault(), "S/ %.2f", plato.precio)

            if (plato.disponible == 1) {
                binding.tvDisponiblePlato.text = context.getString(R.string.status_disponible)
                binding.tvDisponiblePlato.setTextColor(
                    ContextCompat.getColor(context, R.color.status_libre)
                )
                binding.tvDisponiblePlato.setBackgroundResource(R.drawable.bg_status_libre)
            } else {
                binding.tvDisponiblePlato.text = context.getString(R.string.status_no_disponible)
                binding.tvDisponiblePlato.setTextColor(
                    ContextCompat.getColor(context, R.color.status_ocupada)
                )
                binding.tvDisponiblePlato.setBackgroundResource(R.drawable.bg_status_ocupada)
            }

            binding.root.setOnClickListener {
                onItemClick?.invoke(plato)
            }
        }
    }
}
