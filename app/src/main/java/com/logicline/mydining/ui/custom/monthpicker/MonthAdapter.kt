package com.logicline.mydining.ui.custom.monthpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.data.models.Month

class MonthAdapter(
    private var months: List<Month>,
    private var selectedId: Int?,
    private val onSelect: (Month) -> Unit
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    private var filtered = months

    fun updateSelectedId(id: Int?) {
        selectedId = id
        notifyDataSetChanged()
    }

    inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.monthName)
        val check = view.findViewById<ImageView>(R.id.selectedIcon)

        fun bind(month: Month) {
            name.text = month.name
            check.visibility = if (month.id == selectedId) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                onSelect(month)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        return MonthViewHolder(view)
    }

    override fun getItemCount(): Int = filtered.size

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(filtered[position])
    }

    fun filter(query: String) {
        filtered = if (query.isBlank()) months
        else months.filter { it.name.contains(query, ignoreCase = true) }
        notifyDataSetChanged()
    }
}
