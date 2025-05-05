package com.logicline.mydining.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.SearchableStringAdapter

class SearchableListDialog(
    private val context: Context,
    private val title: String = "Select Item",
    private val items: List<String>,
    private val onItemSelected: (List<String>) -> Unit,
    private val supportMultiSelect: Boolean = false,
    private val preSelectedItems: List<String> = emptyList()
) {

    private lateinit var dialog: Dialog
    private lateinit var adapter: SearchableStringAdapter
    private var filteredItems: List<String> = items
    private val selectedItems = preSelectedItems.toMutableList()
    private var lastSearchQuery: String = ""

    fun show() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_searchable_list)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.setTitle(title)

        val searchView = dialog.findViewById<SearchView>(R.id.searchView)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val btnDone = dialog.findViewById<Button>(R.id.btnDone)

        adapter = SearchableStringAdapter(filteredItems, selectedItems) { clickedItem ->
            handleItemSelection(clickedItem)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Multi-select done button visibility
        btnDone?.visibility = if (supportMultiSelect) View.VISIBLE else View.GONE

        // Done button click
        btnDone?.setOnClickListener {
            onItemSelected(selectedItems)
            dialog.dismiss()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                lastSearchQuery = newText.orEmpty()
                filterItems(lastSearchQuery)
                return true
            }
        })

        searchView.setQuery(lastSearchQuery, false)
        dialog.show()
    }

    private fun handleItemSelection(selected: String) {
        if (supportMultiSelect) {
            if (selectedItems.contains(selected)) {
                selectedItems.remove(selected)
            } else {
                selectedItems.add(selected)
            }
            adapter.updateSelectedItems(selectedItems)
        } else {
            selectedItems.clear()
            selectedItems.add(selected)
            onItemSelected(selectedItems)
            dialog.dismiss()
        }
    }

    private fun filterItems(query: String) {
        filteredItems = items.filter {
            it.contains(query, ignoreCase = true)
        }
        adapter.filterList(filteredItems)
    }

    fun getSelectedItems(): List<String> = selectedItems

    fun setPreSelected(items: List<String>) {
        selectedItems.clear()
        selectedItems.addAll(items)
        filterItems(lastSearchQuery)
    }

    fun saveLastSearch(query: String) {
        lastSearchQuery = query
    }
}
