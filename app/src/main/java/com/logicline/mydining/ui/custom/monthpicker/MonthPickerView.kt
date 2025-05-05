package com.logicline.mydining.ui.custom.monthpicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.domains.MonthStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MonthPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private val recyclerView: RecyclerView
    private val progressBar: ProgressBar
    private val searchBox: EditText
    private var adapter: MonthAdapter? = null
    private var selectedMonthId: Int? = null
    private var onMonthSelected: ((Month) -> Unit)? = null

    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.dialog_month_selector, this, true)
        recyclerView = findViewById(R.id.monthRecycler)
        progressBar = findViewById(R.id.progressBar)
        searchBox = findViewById(R.id.searchBox)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun initialize(monthRepository: MonthRepository, preselectedId: Int? = null, onSelected: ((Month) -> Unit)? = null) {
        this.selectedMonthId = preselectedId
        this.onMonthSelected = onSelected

        viewScope.launch {
            MonthStore.fetchMonthsIfNeeded(monthRepository)

            MonthStore.state.collectLatest { state ->
                when (state) {
                    is DataState.Loading -> {
                        progressBar.isVisible = true
                        recyclerView.isVisible = false
                    }
                    is DataState.Success -> {
                        progressBar.isVisible = false
                        recyclerView.isVisible = true
                        setUpAdapter(state.data ?: emptyList())
                    }
                    is DataState.Error -> {
                        progressBar.isVisible = false
                        Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }

                    is DataState.Idle->{}
                }
            }
        }
    }

    private fun setUpAdapter(months: List<Month>) {
        adapter = MonthAdapter(months, selectedMonthId) { selected ->
            selectedMonthId = selected.id
            onMonthSelected?.invoke(selected)
            adapter?.updateSelectedId(selected.id)
        }
        recyclerView.adapter = adapter

        searchBox.addTextChangedListener {
            adapter?.filter(it.toString())
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewScope.cancel() // prevent memory leaks
    }
}