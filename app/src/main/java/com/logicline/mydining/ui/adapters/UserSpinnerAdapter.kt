package com.logicline.mydining.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.logicline.mydining.R
import com.logicline.mydining.data.models.MessUser

data class UserSpinnerItem(
    val messUser: MessUser?,
    val displayText: String,
    val isEnabled: Boolean = true,
    val isHeader: Boolean = false
)

class UserSpinnerAdapter(
    private val context: Context,
    private val items: List<UserSpinnerItem>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): UserSpinnerItem = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun isEnabled(position: Int): Boolean = items[position].isEnabled

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.user_spinner_selected_item, parent, false)
        val item = items[position]
        
        val textView = view.findViewById<TextView>(R.id.tvSelectedText)
        val iconView = view.findViewById<ImageView>(R.id.ivSelectedIcon)
        
        textView.text = item.displayText
        
        // Set icon based on item type
        when {
            item.isHeader -> {
                iconView.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                iconView.visibility = View.VISIBLE
            }
            item.messUser != null -> {
                iconView.setImageResource(R.drawable.ic_person_24)
                iconView.visibility = View.VISIBLE
            }
            else -> {
                iconView.visibility = View.GONE
            }
        }
        
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.user_spinner_dropdown_item, parent, false)
        val item = items[position]
        
        val textView = view.findViewById<TextView>(R.id.tvDropdownText)
        val iconView = view.findViewById<ImageView>(R.id.ivDropdownIcon)
        val dividerView = view.findViewById<View>(R.id.divider)
        
        textView.text = item.displayText
        
        // Style based on item state
        when {
            item.isHeader -> {
                textView.setTextColor(context.getColor(R.color.colorPrimary))
                textView.textSize = 16f
                iconView.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                iconView.visibility = View.VISIBLE
                view.alpha = 1.0f
                dividerView.visibility = View.VISIBLE
            }
            !item.isEnabled -> {
                textView.setTextColor(context.getColor(R.color.disabled_text_color))
                textView.textSize = 14f
                iconView.setImageResource(R.drawable.ic_person_off_24)
                iconView.visibility = View.VISIBLE
                view.alpha = 0.5f
                dividerView.visibility = View.GONE
            }
            else -> {
                textView.setTextColor(context.getColor(R.color.enabled_text_color))
                textView.textSize = 14f
                iconView.setImageResource(R.drawable.ic_person_24)
                iconView.visibility = View.VISIBLE
                view.alpha = 1.0f
                dividerView.visibility = View.GONE
            }
        }
        
        return view
    }

    override fun areAllItemsEnabled(): Boolean = false

    // Get the actual MessUser from selected position (excluding disabled items)
    fun getSelectedMessUser(position: Int): MessUser? {
        Log.d("UserSpinnerAdapter", "getSelectedMessUser called with position: $position")
        
        if (position < 0 || position >= items.size) {
            Log.w("UserSpinnerAdapter", "Position $position is out of bounds (size: ${items.size})")
            return null
        }
        
        val item = items[position]
        Log.d("UserSpinnerAdapter", "Item at position $position: ${item.displayText}, enabled: ${item.isEnabled}, isHeader: ${item.isHeader}")
        
        return if (item.isEnabled && !item.isHeader) {
            Log.d("UserSpinnerAdapter", "Returning MessUser: ${item.messUser?.user?.name}")
            item.messUser
        } else {
            Log.d("UserSpinnerAdapter", "Item is disabled or header, returning null")
            null
        }
    }
}