package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import com.google.android.material.card.MaterialCardView
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
        val parentWidth = parent.measuredWidth.takeIf { it > 0 } ?: parent.resources.displayMetrics.widthPixels
        val spacingPx = (parent.resources.displayMetrics.density * 8).toInt()
        val rawSize = (parentWidth - spacingPx * 8) / 7
        val minSize = (parent.resources.displayMetrics.density * 64).toInt()
        val cellSize = maxOf(rawSize, minSize)
        val params = RecyclerView.LayoutParams(cellSize, cellSize)
        view.layoutParams = params
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = getItem(position)
        val dayNumber = date.get(Calendar.DAY_OF_MONTH)

        holder.dayNumber.text = dayNumber.toString()

        val isToday = sameDay(date, currentDate)
        val isSelected = sameDay(date, selectedDate)
        val rowIndex = position / 7

        val type = typeProvider(date)
        val colorRes = when (type) {
            DayType.WORK -> R.color.info_color
            DayType.GAME -> R.color.secondary_color
            DayType.FINANCE -> R.color.asset_blue
            DayType.REST -> R.color.success_color
        }
        holder.colorStrip.setBackgroundResource(colorRes)

        val card = holder.itemView as MaterialCardView
        val density = holder.itemView.resources.displayMetrics.density
        val strokeBase = (density * 1).toInt().coerceAtLeast(1)
        val strokeWeek = (density * 3).toInt().coerceAtLeast(2)
        val strokeSelected = (density * 4).toInt().coerceAtLeast(3)
        val accentWeekColor = holder.itemView.context.getColor(R.color.secondary_variant)
        val normalStrokeColor = holder.itemView.context.getColor(R.color.primary_color)
        val todayStrokeColor = holder.itemView.context.getColor(R.color.primary_variant)

        card.strokeWidth = when {
            isSelected -> strokeSelected
            rowIndex == 1 -> strokeWeek
            else -> strokeBase
        }
        card.strokeColor = when {
            isSelected -> todayStrokeColor
            rowIndex == 1 -> accentWeekColor
            isToday -> todayStrokeColor
            else -> normalStrokeColor
        }
        card.cardElevation = if (isSelected) 10f else 4f
        if (isSelected) {
            card.animate().scaleX(1.06f).scaleY(1.06f).setDuration(120).start()
            holder.lottie?.apply {
                visibility = View.VISIBLE
                progress = 0f
                playAnimation()
                postDelayed({ visibility = View.GONE }, 400)
            }
        } else {
            card.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
            holder.lottie?.visibility = View.GONE
        }

        holder.playerToken.isVisible = isToday

        card.contentDescription = "День $dayNumber: ${type.name}"

        card.setOnClickListener {
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
        val container: FrameLayout = view.findViewById(R.id.dayContainer)
        val dayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val playerToken: ImageView = view.findViewById(R.id.ivPlayerToken)
        val colorStrip: View = view.findViewById(R.id.vColorStrip)
        val lottie: com.airbnb.lottie.LottieAnimationView? = view.findViewById(R.id.lottieDaySelect)
    }
}