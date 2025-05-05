package com.logicline.mydining.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R

class SearchableStringAdapter(
    private var items: List<String>,
    private var selectedItems: List<String> = emptyList(),
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchableStringAdapter.StringViewHolder>() {

    inner class StringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val label: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return StringViewHolder(view)
    }

    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        val item = items[position]
        holder.label.text = item

        // Check if selected
        val isSelected = selectedItems.contains(item)
        val context = holder.itemView.context

        // Apply selected style
        holder.label.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        holder.label.setTextColor(
            ContextCompat.getColor(
                context,
                if (isSelected) R.color.teal_700 else android.R.color.black
            )
        )
        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (isSelected) R.color.teal_200 else android.R.color.transparent
            )
        )

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun filterList(filtered: List<String>) {
        items = filtered
        notifyDataSetChanged()
    }

    fun updateSelectedItems(newSelected: List<String>) {
        selectedItems = newSelected
        notifyDataSetChanged()
    }
}
