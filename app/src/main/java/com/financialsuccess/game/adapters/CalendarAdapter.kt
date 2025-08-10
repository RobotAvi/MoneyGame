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
    private val year: Int,
    private val monthZeroBased: Int,
    private val currentDayOfMonth: Int,
    private val playerDayOfMonth: Int,
    private val iconProvider: (day: Int) -> Int
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val daysInMonth: Int
    private val leadingEmpty: Int
    private val totalCells: Int

    init {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthZeroBased)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        // Calendar.DAY_OF_WEEK: 1=Sunday ... 7=Saturday. We'll make Monday=0.
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        leadingEmpty = when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
        daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // 6 rows max to cover all months in a 7-column grid (42 cells)
        totalCells = ((leadingEmpty + daysInMonth + 6) / 7) * 7
    }

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
        val dayNumber = position - leadingEmpty + 1
        if (dayNumber in 1..daysInMonth) {
            holder.dayNumber.text = dayNumber.toString()
            holder.icon.setImageResource(iconProvider(dayNumber))
            holder.container.background = if (dayNumber == currentDayOfMonth) {
                holder.container.context.getDrawable(R.drawable.bg_day_today)
            } else {
                holder.container.context.getDrawable(R.drawable.bg_day_chip)
            }
            holder.icon.isVisible = true
            holder.dayNumber.isVisible = true
            // Player token on his current day
            holder.playerToken.isVisible = (dayNumber == playerDayOfMonth)
            // Salary day on the 1st could be emphasized by using a special icon if desired
            if (dayNumber == 1) {
                // Optional: use income icon for day 1
                // holder.icon.setImageResource(R.drawable.icon_income)
            }
        } else {
            holder.dayNumber.text = ""
            holder.icon.isVisible = false
            holder.playerToken.isVisible = false
            holder.container.background = holder.container.context.getDrawable(R.drawable.bg_day_chip)
        }
    }

    override fun getItemCount(): Int = totalCells

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: FrameLayout = view as FrameLayout
        val icon: ImageView = view.findViewById(R.id.ivDayIcon)
        val dayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val playerToken: ImageView = view.findViewById(R.id.ivPlayerToken)
    }
}