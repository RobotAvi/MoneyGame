package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.databinding.ItemSkillBinding
import com.financialsuccess.game.models.Skill

class SkillAdapter(
    private val skills: List<Skill>,
    private val onSkillClick: (Skill) -> Unit
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    private val selectedSkills = mutableSetOf<Skill>()

    inner class SkillViewHolder(private val binding: ItemSkillBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(skill: Skill) {
            binding.tvSkillName.text = skill.name
            binding.tvSkillDescription.text = skill.description
            binding.tvSkillBonus.text = "+${skill.salaryBonus}₽"
            binding.tvSkillCategory.text = skill.category.displayName
            
            // Устанавливаем цвет фона в зависимости от выбора
            val isSelected = selectedSkills.contains(skill)
            binding.root.isSelected = isSelected
            
            binding.root.setOnClickListener {
                if (isSelected) {
                    selectedSkills.remove(skill)
                } else {
                    if (selectedSkills.size < 3) { // Максимум 3 навыка
                        selectedSkills.add(skill)
                    }
                }
                onSkillClick(skill)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = ItemSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.bind(skills[position])
    }

    override fun getItemCount(): Int = skills.size
    
    fun getSelectedSkills(): List<Skill> = selectedSkills.toList()
}