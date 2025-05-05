package com.logicline.mydining.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.logicline.mydining.utils.SearchableListDialog

class SearchableSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var title: String = "Select Item"
    private var itemList: List<String> = listOf()
    private var selectedItems: List<String> = listOf()
    private var onSelectionChanged: ((List<String>) -> Unit)? = null
    private var multiSelect: Boolean = false
    private var displayTextViewId: Int? = null

    init {
        setOnClickListener {
            openDialog()
        }
    }

    fun setItems(items: List<String>) {
        itemList = items
    }

    fun setTitle(dialogTitle: String) {
        title = dialogTitle
    }

    fun setMultiSelect(enabled: Boolean) {
        multiSelect = enabled
    }

    fun setDisplayTextViewId(id: Int) {
        displayTextViewId = id
    }

    fun setOnSelectionChangedListener(listener: (List<String>) -> Unit) {
        this.onSelectionChanged = listener
    }

    fun getSelectedItems(): List<String> = selectedItems

    private fun openDialog() {
        val dialog = SearchableListDialog(
            context = context,
            title = title,
            items = itemList,
            supportMultiSelect = multiSelect,
            preSelectedItems = selectedItems,
            onItemSelected = { selected ->
                selectedItems = selected
                updateDisplayedText()
                onSelectionChanged?.invoke(selected)
            }
        )
        dialog.show()
    }

    private fun updateDisplayedText() {
        displayTextViewId?.let {
            val tv = findViewById<TextView>(it)
            tv?.text = selectedItems.joinToString(", ")
        }
    }
}
