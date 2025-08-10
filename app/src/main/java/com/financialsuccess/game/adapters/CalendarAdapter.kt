package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import java.util.Calendar

class CalendarAdapter(
    private val dates: List<Calendar>, // 28 подряд идущих дней (7*4): пред.неделя, текущая, 2 будущих
    private val currentDate: Calendar,
    private val iconProvider: (Calendar) -> Int
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        val parentWidth = parent.measuredWidth.takeIf { it > 0 } ?: parent.resources.displayMetrics.widthPixels
        val spacingPx = (parent.resources.displayMetrics.density * 8).toInt()
        val cellSize = (parentWidth - spacingPx * 8) / 7
        val params = RecyclerView.LayoutParams(cellSize, cellSize)
        view.layoutParams = params
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = dates[position]
        val dayNumber = date.get(Calendar.DAY_OF_MONTH)

        holder.dayNumber.text = dayNumber.toString()
        holder.icon.setImageResource(iconProvider(date))

        val isToday = sameDay(date, currentDate)
        holder.container.background = if (isToday) {
            holder.container.context.getDrawable(R.drawable.bg_day_today)
        } else {
            holder.container.context.getDrawable(R.drawable.bg_day_chip)
        }

        holder.icon.isVisible = true
        holder.dayNumber.isVisible = true
        holder.playerToken.isVisible = isToday
    }

    override fun getItemCount(): Int = dates.size

    private fun sameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: FrameLayout = view as FrameLayout
        val icon: ImageView = view.findViewById(R.id.ivDayIcon)
        val dayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val playerToken: ImageView = view.findViewById(R.id.ivPlayerToken)
    }
}