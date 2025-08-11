package com.financialsuccess.game.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import java.util.Calendar

class CalendarAdapter(
    private val currentDate: Calendar,
    private val typeProvider: (Calendar) -> DayType,
    private var selectedDate: Calendar = currentDate,
    private val onDayClick: (Calendar) -> Unit = {}
) : ListAdapter<Calendar, CalendarAdapter.DayViewHolder>(Diff) {

    enum class DayType { WORK, GAME, FINANCE, REST }

    object Diff : DiffUtil.ItemCallback<Calendar>() {
        override fun areItemsTheSame(oldItem: Calendar, newItem: Calendar): Boolean {
            return oldItem.get(Calendar.YEAR) == newItem.get(Calendar.YEAR) &&
                    oldItem.get(Calendar.DAY_OF_YEAR) == newItem.get(Calendar.DAY_OF_YEAR)
        }
        override fun areContentsTheSame(oldItem: Calendar, newItem: Calendar): Boolean {
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = getItem(position)
        val dayNumber = date.get(Calendar.DAY_OF_MONTH)

        // Устанавливаем номер дня
        holder.dayNumber.text = dayNumber.toString()

        // Проверяем, является ли этот день текущим
        val isToday = sameDay(date, currentDate)
        
        // Показываем токен только на текущем дне
        holder.playerToken.visibility = if (isToday) View.VISIBLE else View.GONE
        
        // Логирование для отладки
        if (isToday) {
            Log.d("CalendarAdapter", "Токен отображается на дне $dayNumber (позиция $position)")
        }

        // Обработка клика
        holder.itemView.setOnClickListener {
            val prev = selectedDate
            selectedDate = date
            val prevIndex = currentList.indexOfFirst { sameDay(it, prev) }
            if (prevIndex >= 0) notifyItemChanged(prevIndex)
            notifyItemChanged(position)
            onDayClick(date)
        }
    }

    private fun sameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val playerToken: ImageView = view.findViewById(R.id.ivPlayerToken)
    }
}