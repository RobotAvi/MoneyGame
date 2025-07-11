package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import com.financialsuccess.game.models.FinancialGoal
import com.google.android.material.button.MaterialButton

class FinancialGoalAdapter(
    private val goals: MutableList<FinancialGoal>,
    private val onRemoveGoal: (Int) -> Unit
) : RecyclerView.Adapter<FinancialGoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGoalName: TextView = itemView.findViewById(R.id.tvGoalName)
        val tvGoalDescription: TextView = itemView.findViewById(R.id.tvGoalDescription)
        val tvGoalDeadline: TextView = itemView.findViewById(R.id.tvGoalDeadline)
        val btnRemoveGoal: MaterialButton = itemView.findViewById(R.id.btnRemoveGoal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_financial_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        
        holder.tvGoalName.text = goal.name
        holder.tvGoalDescription.text = goal.description
        holder.tvGoalDeadline.text = "Дедлайн: ${goal.deadline} месяцев"
        
        holder.btnRemoveGoal.setOnClickListener {
            onRemoveGoal(position)
        }
    }

    override fun getItemCount(): Int = goals.size

    fun updateGoals(newGoals: List<FinancialGoal>) {
        goals.clear()
        goals.addAll(newGoals)
        notifyDataSetChanged()
    }

    fun addGoal(goal: FinancialGoal) {
        goals.add(goal)
        notifyItemInserted(goals.size - 1)
    }

    fun removeGoal(position: Int) {
        if (position in 0 until goals.size) {
            goals.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}