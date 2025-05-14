package com.logicline.mydining.ui.custom.monthpicker

import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import com.logicline.mydining.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.logicline.mydining.data.models.Month
import java.util.Locale

class MonthAdapter(
    private var months: List<Month>,
    private var selectedIds: List<Int> = emptyList(),
    private var config: MonthPickerView.MonthPickerConfig,
    private val onSelect: (Month) -> Unit,
    private val onLongClick: (Month) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filtered = months
    private var highlightedPosition: Int = -1

    fun updateSelectedIds(ids: List<Int>) {
        selectedIds = ids
        notifyDataSetChanged()
    }

    fun updateConfig(newConfig: MonthPickerView.MonthPickerConfig) {
        config = newConfig
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filtered = if (query.isBlank()) months
        else months.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.type.contains(query, ignoreCase = true)
        }
        notifyDataSetChanged()
    }

    fun selectAll(): List<Month> {
        return months
    }

    fun getSelectedMonths(): List<Month> {
        return months.filter { selectedIds.contains(it.id) }
    }

    fun getAllMonths(): List<Month> = months

    override fun getItemViewType(position: Int): Int {
        return if (config.customItemLayout != 0) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(config.customItemLayout, parent, false)
                CustomViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_month, parent, false)
                MonthViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = filtered.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val month = filtered[position]
        when (holder) {
            is MonthViewHolder -> holder.bind(month, selectedIds.contains(month.id), position == highlightedPosition)
            is CustomViewHolder -> holder.bind(month, selectedIds.contains(month.id))
        }
    }

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tv_name)
        private val check: ImageView = view.findViewById(R.id.iv_selected)
        private val statusChip: Chip? = view.findViewById(R.id.chip_status)
        private val dateRange: TextView? = view.findViewById(R.id.tv_date_range)
        private val typeText: TextView? = view.findViewById(R.id.tv_type)
        private val selectionContainer: View? = view.findViewById(R.id.selection_container)
        private val cardView: MaterialCardView? = view.findViewById(R.id.card_view)

        fun bind(month: Month, isSelected: Boolean, isHighlighted: Boolean) {
            name.text = month.name

            // Handle selection style based on configuration
            when (config.selectionMarkStyle) {
                0 -> { // Checkmark
                    selectionContainer?.visibility = if (isSelected) View.VISIBLE else View.GONE
                }
                1 -> { // Radio button
                    check.setImageResource(
                        if (isSelected) R.drawable.check
                        else R.drawable.check
                    )
                    check.visibility = View.VISIBLE
                }
                2 -> { // Highlight
                    cardView?.strokeWidth = if (isSelected) 2.dpToPx() else 0
                    cardView?.strokeColor = itemView.context.getColor(R.color.colorPrimary)
                }
            }

//            // Highlighted state (e.g., for search results)
//            if (isHighlighted) {
//                itemView.setBackgroundColor(itemView.context.getColor(R.color.colorAccent))
//            } else {
//                itemView.background = null
//            }

            // Show additional information when available
            statusChip?.apply {
                text = if (month.isActive) "Active" else "Inactive"
                chipBackgroundColor = ColorStateList.valueOf(
                    itemView.context.getColor(
                        if (month.isActive) R.color.active_bg_color
                        else R.color.inactive_bg_color
                    )
                )
                setTextColor(
                    itemView.context.getColor(
                        if (month.isActive) R.color.active_text_color
                        else R.color.inactive_text_color
                    )
                )
            }

            // Show date range when available
            dateRange?.text = formatDateRange(month.startAt, month.endAt)

            // Show type information
            typeText?.text = month.type

            // Set click listeners
            itemView.setOnClickListener {
                onSelect(month)
            }

            itemView.setOnLongClickListener {
                onLongClick(month)
            }
        }

        private fun formatDateRange(start: String, end: String?): String {
            // Format date range string from ISO dates
            val startDate = try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(start)?.let {
                    SimpleDateFormat("MMM d", Locale.getDefault()).format(it)
                } ?: start
            } catch (e: Exception) {
                start
            }

            val endDate = if (end.isNullOrEmpty()) {
                "Present"
            } else {
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(end)?.let {
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(it)
                    } ?: end
                } catch (e: Exception) {
                    end
                }
            }

            return "$startDate - $endDate"
        }

        private fun Int.dpToPx(): Int {
            return (this * itemView.context.resources.displayMetrics.density).toInt()
        }
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(month: Month, isSelected: Boolean) {
            // For custom layouts, find views by ID and bind data
            // This provides flexibility for completely custom item layouts
            itemView.setOnClickListener {
                onSelect(month)
            }

            itemView.setOnLongClickListener {
                onLongClick(month)
            }
        }
    }
}