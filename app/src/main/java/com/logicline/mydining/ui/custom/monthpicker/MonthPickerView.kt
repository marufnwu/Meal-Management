package com.logicline.mydining.ui.custom.monthpicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.domains.MonthStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MonthPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    // Existing components
    private val recyclerView: RecyclerView
    private val progressBar: ProgressBar
    private val searchBox: EditText
    private val clearButton: ImageView
    private val titleText: TextView

    // Additional layout components
    private val headerLayout: ConstraintLayout
    private val filterContainer: FrameLayout
    private val emptyStateView: View

    // Configuration options
    private var config = MonthPickerConfig()
    private var adapter: MonthAdapter? = null
    private var selectedMonthId: Int? = null
    private var onMonthSelected: ((Month) -> Unit)? = null
    private var onMonthLongPressed: ((Month) -> Boolean)? = null
    private var onFilterApplied: ((List<Month>) -> Unit)? = null

    // State management
    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var monthsSortingStrategy: SortingStrategy = SortingStrategy.NEWEST_FIRST
    private var monthsFilterStrategy: FilterStrategy = FilterStrategy.ALL
    private var searchDebouncer = Debouncer(viewScope, 300L)

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.dialog_month_selector, this, true)

        // Initialize views
        recyclerView = findViewById(R.id.monthRecycler)
        progressBar = findViewById(R.id.progressBar)
        searchBox = findViewById(R.id.searchBox)
        clearButton = findViewById(R.id.clearSearchBtn)
        titleText = findViewById(R.id.titleText)
        headerLayout = findViewById(R.id.headerLayout)
        filterContainer = findViewById(R.id.filterContainer)
        emptyStateView = findViewById(R.id.emptyState)

        // Apply custom attributes if provided
        context.obtainStyledAttributes(attrs, R.styleable.MonthPickerView).apply {
            try {
                // Apply custom styling from XML
                config.showTitle = getBoolean(R.styleable.MonthPickerView_showTitle, true)
                config.showSearch = getBoolean(R.styleable.MonthPickerView_showSearch, true)
                config.title = getString(R.styleable.MonthPickerView_title) ?: "Select Month"
                config.searchHint = getString(R.styleable.MonthPickerView_searchHint) ?: "Search months..."
                config.multipleSelection = getBoolean(R.styleable.MonthPickerView_multipleSelection, false)
                config.selectionMarkStyle = getInt(R.styleable.MonthPickerView_selectionMarkStyle, 0)
                config.gridLayout = getBoolean(R.styleable.MonthPickerView_gridLayout, false)
            } finally {
                recycle()
            }
        }

        setupViews()
    }

    private fun setupViews() {
        // Apply configuration
        titleText.isVisible = config.showTitle
        titleText.text = config.title
        searchBox.isVisible = config.showSearch
        searchBox.hint = config.searchHint

        // Configure RecyclerView layout based on settings
        recyclerView.layoutManager = if (config.gridLayout) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }

        // Search functionality with debounce
        searchBox.addTextChangedListener {
            clearButton.isVisible = !it.isNullOrEmpty()
            searchDebouncer.debounce {
                adapter?.filter(it.toString())
            }
        }

        clearButton.setOnClickListener {
            searchBox.text.clear()
        }

        // Setup animations
        setupAnimations()
    }

    private fun setupAnimations() {
        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 300 }
        val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 200 }

        recyclerView.animation = fadeIn
    }

    fun initialize(
        monthRepository: MonthRepository,
        preselectedIds: List<Int>? = null,
        config: MonthPickerConfig.() -> Unit = {},
        onSelected: (Month) -> Unit = {}
    ) {
        // Apply custom configuration
        this.config.apply(config)
        this.selectedMonthId = preselectedIds?.firstOrNull()
        this.onMonthSelected = onSelected

        // Re-apply configuration in case it was updated
        setupViews()

        viewScope.launch {
            MonthStore.fetchMonthsIfNeeded(monthRepository)

            MonthStore.state.collectLatest { state ->
                handleDataState(state)
            }
        }
    }

    private fun handleDataState(state: DataState<List<Month>>) {
        when (state) {
            is DataState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                emptyStateView.isVisible = false
            }
            is DataState.Success -> {
                progressBar.isVisible = false
                val months = state.data ?: emptyList()
                if (months.isEmpty()) {
                    recyclerView.isVisible = false
                    emptyStateView.isVisible = true
                } else {
                    recyclerView.isVisible = true
                    emptyStateView.isVisible = false
                    setUpAdapter(applySorting(applyFilter(months)))
                }
            }
            is DataState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                emptyStateView.isVisible = true
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {} // Handle Idle and Exception states
        }
    }

    private fun applyFilter(months: List<Month>): List<Month> {
        return when (monthsFilterStrategy) {
            FilterStrategy.ALL -> months
            FilterStrategy.ACTIVE_ONLY -> months.filter { it.isActive }
            FilterStrategy.INACTIVE_ONLY -> months.filter { !it.isActive }
            is FilterStrategy.CUSTOM -> (monthsFilterStrategy as FilterStrategy.CUSTOM).filter(months)
        }
    }

    private fun applySorting(months: List<Month>): List<Month> {
        return when (monthsSortingStrategy) {
            SortingStrategy.ALPHABETICAL -> months.sortedBy { it.name }
            SortingStrategy.NEWEST_FIRST -> months.sortedByDescending { it.startAt }
            SortingStrategy.OLDEST_FIRST -> months.sortedBy { it.startAt }
            is SortingStrategy.CUSTOM -> (monthsSortingStrategy as SortingStrategy.CUSTOM).sort(months)
        }
    }

    private fun setUpAdapter(months: List<Month>) {
        adapter = MonthAdapter(
            months = months,
            selectedIds = if (config.multipleSelection)
                selectedMonthIds.toList()
            else listOfNotNull(selectedMonthId),
            config = config,
            onSelect = { selected ->
                if (config.multipleSelection) {
                    toggleMonthSelection(selected)
                } else {
                    selectedMonthId = selected.id
                    onMonthSelected?.invoke(selected)
                }
                adapter?.updateSelectedIds((if (config.multipleSelection) selectedMonthIds else listOfNotNull(selectedMonthId)) as List<Int>)
            },
            onLongClick = { month ->
                onMonthLongPressed?.invoke(month) ?: false
            }
        )
        recyclerView.adapter = adapter
    }

    // Public API for configuration

    fun setFilterStrategy(strategy: FilterStrategy) {
        monthsFilterStrategy = strategy
        refreshData()
    }

    fun setSortingStrategy(strategy: SortingStrategy) {
        monthsSortingStrategy = strategy
        refreshData()
    }

    fun setMultipleSelection(enabled: Boolean) {
        config.multipleSelection = enabled
        adapter?.updateConfig(config)
    }

    fun getSelectedMonths(): List<Month> {
        return adapter?.getSelectedMonths() ?: emptyList()
    }

    fun setOnMonthLongPressListener(listener: (Month) -> Boolean) {
        onMonthLongPressed = listener
    }

    fun setOnFilterAppliedListener(listener: (List<Month>) -> Unit) {
        onFilterApplied = listener
    }

    // Multiple selection support
    private val selectedMonthIds = mutableSetOf<Int>()

    private fun toggleMonthSelection(month: Month) {
        if (selectedMonthIds.contains(month.id)) {
            selectedMonthIds.remove(month.id)
        } else {
            selectedMonthIds.add(month.id)
        }
        onFilterApplied?.invoke(adapter?.getSelectedMonths() ?: emptyList())
    }

    fun selectAll() {
        if (config.multipleSelection) {
            adapter?.selectAll()?.forEach { selectedMonthIds.add(it.id) }
            adapter?.updateSelectedIds(selectedMonthIds.toList())
        }
    }

    fun clearSelection() {
        selectedMonthIds.clear()
        adapter?.updateSelectedIds(emptyList())
    }

    /**
     * Sets the currently selected month by ID after initialization
     * @param monthId The ID of the month to select
     * @param triggerCallback Whether to trigger the onMonthSelected callback
     */
    fun setSelectedMonthId(monthId: Int?, triggerCallback: Boolean = true) {
        if (!config.multipleSelection) {
            this.selectedMonthId = monthId
            adapter?.updateSelectedIds(listOfNotNull(monthId))

//            if (triggerCallback && monthId != null) {
//                // Find the month object and trigger callback
//                adapter?.getAllMonths()?.find { it.id == monthId }?.let { month ->
//                    onMonthSelected?.invoke(month)
//                }
//            }
        }
    }

    /**
     * Sets multiple selected months by IDs after initialization
     * Only works if multipleSelection is enabled
     * @param monthIds List of month IDs to select
     */
    fun setSelectedMonthIds(monthIds: List<Int>) {
        if (config.multipleSelection) {
            selectedMonthIds.clear()
            selectedMonthIds.addAll(monthIds)
            adapter?.updateSelectedIds(monthIds)
        }
    }

    /**
     * Selects a specific month by object reference
     * Useful when you have the Month object but not just the ID
     * @param month The Month object to select
     */
    fun selectMonth(month: Month) {
        if (config.multipleSelection) {
            if (!selectedMonthIds.contains(month.id)) {
                selectedMonthIds.add(month.id)
                adapter?.updateSelectedIds(selectedMonthIds.toList())
            }
        } else {
            selectedMonthId = month.id
            onMonthSelected?.invoke(month)
            adapter?.updateSelectedIds(listOfNotNull(month.id))
        }
    }

    /**
     * Gets the currently selected month ID in single selection mode
     * @return The ID of the selected month or null if none selected
     */
    fun getSelectedMonthId(): Int? {
        return selectedMonthId
    }

    /**
     * Gets all currently selected month IDs in multiple selection mode
     * @return List of selected month IDs
     */
    fun getSelectedMonthIds(): List<Int> {
        return selectedMonthIds.toList()
    }

    /**
     * Checks if a specific month is currently selected
     * @param monthId The ID of the month to check
     * @return true if the month is selected, false otherwise
     */
    fun isMonthSelected(monthId: Int): Boolean {
        return if (config.multipleSelection) {
            selectedMonthIds.contains(monthId)
        } else {
            selectedMonthId == monthId
        }
    }

    private fun refreshData() {
        val currentAdapter = adapter ?: return
        val months = currentAdapter.getAllMonths()
        setUpAdapter(applySorting(applyFilter(months)))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        searchDebouncer.cancel()
        viewScope.cancel() // prevent memory leaks
    }

    // Configuration and Strategy classes

    sealed class FilterStrategy {
        object ALL : FilterStrategy()
        object ACTIVE_ONLY : FilterStrategy()
        object INACTIVE_ONLY : FilterStrategy()
        class CUSTOM(val filter: (List<Month>) -> List<Month>) : FilterStrategy()
    }

    sealed class SortingStrategy {
        object ALPHABETICAL : SortingStrategy()
        object NEWEST_FIRST : SortingStrategy()
        object OLDEST_FIRST : SortingStrategy()
        class CUSTOM(val sort: (List<Month>) -> List<Month>) : SortingStrategy()
    }

    class MonthPickerConfig {
        var showTitle: Boolean = true
        var showSearch: Boolean = true
        var title: String = "Select Month"
        var searchHint: String = "Search months..."
        var multipleSelection: Boolean = false
        var selectionMarkStyle: Int = 0 // 0 = checkmark, 1 = radio button, 2 = highlight
        var gridLayout: Boolean = false
        var customItemLayout: Int = 0
    }

    // Simple debouncer for search functionality
    private class Debouncer(
        private val coroutineScope: CoroutineScope,
        private val delayMillis: Long
    ) {
        private var searchJob: Job? = null

        fun debounce(action: () -> Unit) {
            searchJob?.cancel()
            searchJob = coroutineScope.launch {
                delay(delayMillis)
                action()
            }
        }

        fun cancel() {
            searchJob?.cancel()
        }
    }
}