package com.senati.proyecto_restaurante.ui.mesas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.senati.proyecto_restaurante.R
import com.senati.proyecto_restaurante.data.model.Mesa
import com.senati.proyecto_restaurante.databinding.ItemMesaBinding

class MesasAdapter(
    private var mesas: List<Mesa> = emptyList(),
    private val onItemClick: ((Mesa) -> Unit)? = null
) : RecyclerView.Adapter<MesasAdapter.MesaViewHolder>() {

    fun updateData(newMesas: List<Mesa>) {
        this.mesas = newMesas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        holder.bind(mesas[position])
    }

    override fun getItemCount(): Int = mesas.size

    inner class MesaViewHolder(private val binding: ItemMesaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mesa: Mesa) {
            val context = itemView.context

            binding.tvNumeroMesa.text = context.getString(R.string.mesa_label, mesa.numero)
            binding.tvCapacidadMesa.text = context.getString(R.string.capacidad_label, mesa.capacidad)
            binding.tvEstadoMesa.text = mesa.estado.uppercase()

            if (mesa.estado.equals("LIBRE", ignoreCase = true)) {
                binding.tvEstadoMesa.setTextColor(
                    ContextCompat.getColor(context, R.color.status_libre)
                )
                binding.tvEstadoMesa.setBackgroundResource(R.drawable.bg_status_libre)
                binding.ivMesaIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_libre))
            } else {
                binding.tvEstadoMesa.setTextColor(
                    ContextCompat.getColor(context, R.color.status_ocupada)
                )
                binding.tvEstadoMesa.setBackgroundResource(R.drawable.bg_status_ocupada)
                binding.ivMesaIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_ocupada))
            }

            binding.root.setOnClickListener {
                onItemClick?.invoke(mesa)
            }
        }
    }
}

