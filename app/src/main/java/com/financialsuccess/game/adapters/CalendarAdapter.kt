package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R

class CalendarAdapter(
    private val dayIcons: List<Int>,
    private val todayIndex: Int
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        // Make each cell square and evenly distributed in a 7-column grid
        val parentWidth = parent.measuredWidth.takeIf { it > 0 } ?: parent.resources.displayMetrics.widthPixels
        val spacingPx = (parent.resources.displayMetrics.density * 8).toInt() // approx margins
        val cellSize = (parentWidth - spacingPx * 8) / 7 // 7 columns + margins
        val params = RecyclerView.LayoutParams(cellSize, cellSize)
        view.layoutParams = params
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val iconRes = dayIcons[position % dayIcons.size]
        holder.icon.setImageResource(iconRes)
        holder.dayNumber.text = (position + 1).toString()
        holder.container.background = if (position == todayIndex) {
            holder.container.context.getDrawable(R.drawable.bg_day_today)
        } else {
            holder.container.context.getDrawable(R.drawable.bg_day_chip)
        }
    }

    override fun getItemCount(): Int = dayIcons.size

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: FrameLayout = view as FrameLayout
        val icon: ImageView = view.findViewById(R.id.ivDayIcon)
        val dayNumber: TextView = view.findViewById(R.id.tvDayNumber)
    }
}