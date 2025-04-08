package com.logicline.mydining.utils

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.SearchView
import com.logicline.mydining.R
import com.logicline.mydining.adapter.SearchableStringAdapter
class SearchableListDialog(
    private val context: Context,
    private val title: String = "Select Item",
    private val items: List<String>,
    private val onItemSelected: (List<String>) -> Unit, // Multi-select callback
    private val supportMultiSelect: Boolean = false, // Flag for multi-select
    private val preSelectedItems: List<String> = emptyList() // Pre-selected items
) {

    private lateinit var dialog: Dialog
    private lateinit var adapter: SearchableStringAdapter
    private var filteredItems: List<String> = items
    private var selectedItems: MutableList<String> = preSelectedItems.toMutableList()
    private var lastSearchQuery: String = ""

    // Show the dialog
    fun show() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_searchable_list)
        dialog.setTitle(title)

        val searchView = dialog.findViewById<SearchView>(R.id.searchView)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)

        adapter = SearchableStringAdapter(filteredItems) { selected ->
            handleItemSelection(selected)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                lastSearchQuery = newText.orEmpty()
                filterItems(lastSearchQuery)
                return true
            }
        })

        // Restore last search query
        searchView.setQuery(lastSearchQuery, false)

        dialog.show()
    }

    // Handle item selection based on multi-select flag
    private fun handleItemSelection(selected: String) {
        if (supportMultiSelect) {
            if (selectedItems.contains(selected)) {
                selectedItems.remove(selected)
            } else {
                selectedItems.add(selected)
            }
        } else {
            selectedItems.clear()
            selectedItems.add(selected)
            dialog.dismiss() // Automatically dismiss on single select
        }

        onItemSelected(selectedItems) // Call the callback with selected items
    }

    // Filter items based on search query
    private fun filterItems(query: String) {
        filteredItems = items.filter {
            it.contains(query, ignoreCase = true)
        }
        adapter.filterList(filteredItems)
    }

    // Get the selected items (useful for multi-select mode)
    fun getSelectedItems(): List<String> {
        return selectedItems
    }

    // Get the selected item (useful for single select mode)
    fun getSelectedItem(): String? {
        return if (selectedItems.isNotEmpty()) selectedItems.first() else null
    }

    // Set pre-selected item(s)
    fun setPreSelected(items: List<String>) {
        selectedItems.clear()
        selectedItems.addAll(items)
        filterItems(lastSearchQuery) // Refresh the filtered list based on the search
    }

    // Save the last search query
    fun saveLastSearch(query: String) {
        lastSearchQuery = query
    }
}