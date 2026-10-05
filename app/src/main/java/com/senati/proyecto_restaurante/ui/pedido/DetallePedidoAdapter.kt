package com.senati.proyecto_restaurante.ui.pedido

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.proyecto_restaurante.data.model.DetallePedido
import com.senati.proyecto_restaurante.databinding.ItemDetallePedidoBinding
import java.util.Locale

class DetallePedidoAdapter(
    private var detalles: List<DetallePedido> = emptyList()
) : RecyclerView.Adapter<DetallePedidoAdapter.DetalleViewHolder>() {

    fun updateData(newDetalles: List<DetallePedido>) {
        this.detalles = newDetalles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetallePedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        holder.bind(detalles[position])
    }

    override fun getItemCount(): Int = detalles.size

    inner class DetalleViewHolder(private val binding: ItemDetallePedidoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetallePedido) {
            binding.tvCantidadItem.text = String.format(Locale.getDefault(), "%dx", item.cantidad)
            binding.tvNombrePlatoItem.text = item.nombrePlato ?: "Plato #${item.idPlato}"
            binding.tvPrecioUnitItem.text = String.format(Locale.getDefault(), "Unit: S/ %.2f", item.precioUnit)
            binding.tvSubtotalItem.text = String.format(Locale.getDefault(), "S/ %.2f", item.subtotal)
        }
    }
}
