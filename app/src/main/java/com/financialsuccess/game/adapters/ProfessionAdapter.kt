package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import com.financialsuccess.game.databinding.ItemProfessionBinding
import com.financialsuccess.game.models.Profession
import java.text.NumberFormat
import java.util.*

class ProfessionAdapter(
    private val professions: List<Profession>,
    private val onProfessionSelected: (Profession) -> Unit
) : RecyclerView.Adapter<ProfessionAdapter.ProfessionViewHolder>() {
    
    private var selectedPosition = -1
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    inner class ProfessionViewHolder(private val binding: ItemProfessionBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(profession: Profession, isSelected: Boolean) {
            binding.tvProfessionName.text = profession.name
            binding.tvProfessionDescription.text = profession.description
            binding.tvSalary.text = "Зарплата: ${currencyFormat.format(profession.salary)}"
            val total = profession.expenses + profession.taxes
            binding.tvExpenses.text = "Расходы: ${currencyFormat.format(profession.expenses)}  Налоги: ${currencyFormat.format(profession.taxes)}  Всего: ${currencyFormat.format(total)}"
            binding.tvEducation.text = "Образование: ${profession.education}"
            
            // Устанавливаем иконку профессии
            val iconRes = when (profession.id) {
                "teacher" -> R.drawable.profession_teacher
                "engineer" -> R.drawable.profession_engineer  
                "doctor" -> R.drawable.profession_doctor
                "manager" -> R.drawable.profession_manager
                "mechanic" -> R.drawable.profession_mechanic
                "lawyer" -> R.drawable.profession_lawyer
                else -> R.drawable.profession_teacher
            }
            
            try {
                val iconField = binding.javaClass.getDeclaredField("ivProfessionIcon")
                val imageView = iconField.get(binding) as? android.widget.ImageView
                imageView?.setBackgroundResource(iconRes)
            } catch (e: Exception) {
                // Игнорируем если поле не найдено (совместимость)
            }
            
            binding.cardProfession.isChecked = isSelected
            
            binding.cardProfession.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onProfessionSelected(profession)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionViewHolder {
        val binding = ItemProfessionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProfessionViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProfessionViewHolder, position: Int) {
        holder.bind(professions[position], position == selectedPosition)
    }
    
    override fun getItemCount() = professions.size
}