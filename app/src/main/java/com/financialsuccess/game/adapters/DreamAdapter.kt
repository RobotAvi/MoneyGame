package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.databinding.ItemDreamBinding
import com.financialsuccess.game.models.Dream
import java.text.NumberFormat
import java.util.*

class DreamAdapter(
    private val dreams: List<Dream>,
    private val onDreamSelected: (Dream) -> Unit
) : RecyclerView.Adapter<DreamAdapter.DreamViewHolder>() {
    
    private var selectedPosition = -1
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    inner class DreamViewHolder(private val binding: ItemDreamBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(dream: Dream, isSelected: Boolean) {
            binding.tvDreamName.text = dream.name
            binding.tvDreamDescription.text = dream.description
            binding.tvCost.text = "Стоимость: ${currencyFormat.format(dream.cost)}"
            binding.tvCashFlowRequired.text = "Необходимый поток: ${currencyFormat.format(dream.cashFlowRequired)}"
            
            binding.cardDream.isChecked = isSelected
            
            binding.cardDream.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onDreamSelected(dream)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamViewHolder {
        val binding = ItemDreamBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DreamViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: DreamViewHolder, position: Int) {
        holder.bind(dreams[position], position == selectedPosition)
    }
    
    override fun getItemCount() = dreams.size
}