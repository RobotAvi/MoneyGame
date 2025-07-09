package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import com.financialsuccess.game.databinding.ItemFinancialEntryBinding
import com.financialsuccess.game.models.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FinancialJournalAdapter(
    private val entries: List<FinancialEntry>
) : RecyclerView.Adapter<FinancialJournalAdapter.FinancialEntryViewHolder>() {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    private val dateFormat = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialEntryViewHolder {
        val binding = ItemFinancialEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FinancialEntryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FinancialEntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }
    
    override fun getItemCount(): Int = entries.size
    
    inner class FinancialEntryViewHolder(
        private val binding: ItemFinancialEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(entry: FinancialEntry) {
            binding.apply {
                // Иконка и название категории
                tvCategoryIcon.text = entry.category.getIcon()
                tvCategoryName.text = entry.category.getDisplayName()
                
                // Описание операции
                tvDescription.text = entry.description
                
                // Сумма с правильным цветом
                val isPositive = entry.amount > 0
                tvAmount.text = if (isPositive) {
                    "+${currencyFormat.format(entry.amount)}"
                } else {
                    currencyFormat.format(entry.amount)
                }
                
                tvAmount.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (isPositive) R.color.success_color else R.color.error_color
                    )
                )
                
                // Время и возраст
                val time = dateFormat.format(Date(entry.timestamp))
                tvTimeAge.text = "$time • ${entry.playerAge} лет"
                
                // Полная дата
                tvMonth.text = if (entry.realDate.isNotEmpty()) {
                    entry.realDate
                } else {
                    "Месяц ${entry.monthNumber}"
                }
                
                // Баланс после операции
                tvBalanceAfter.text = "Баланс: ${currencyFormat.format(entry.balanceAfter)}"
                
                // Фон карточки в зависимости от типа
                val cardBackground = if (isPositive) {
                    ContextCompat.getColor(root.context, R.color.success_background)
                } else {
                    ContextCompat.getColor(root.context, R.color.error_background)
                }
                
                root.setCardBackgroundColor(cardBackground)
            }
        }
    }
}