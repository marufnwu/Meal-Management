package com.logicline.mydining.ui.custom.monthpicker

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.logicline.mydining.MyApplication
import com.logicline.mydining.R
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.domains.MonthStore
import com.logicline.mydining.ui.custom.GenericDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonthPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
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
    private val btnAdd: MaterialButton

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

    private var monthCreateDialog: GenericDialog? = null

    private var monthRepository: MonthRepository

    init {
        orientation = VERTICAL
        Log.d("MonthPickerView", "Initializing")
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
        btnAdd = findViewById(R.id.btn_add)

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

        val app = context.applicationContext as? MyApplication
        if (app != null) {
            monthRepository = MonthRepository(app.myApi)
            Log.d("MonthPickerView", "Repository initialized")
        } else {
            Log.e("MonthPickerView", "Failed to get application context")
            throw IllegalStateException("Could not get application context")
        }

        setupViews()
        // Remove default initialization to prevent double loading
        // initialize()
    }

    private fun setupViews() {
        Log.d("MonthPickerView", "Setting up views")
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

        btnAdd.setOnClickListener {
            showCreateMonthDialog()
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
        preselectedIds: List<Int>? = null,
        config: MonthPickerConfig.() -> Unit = {},
        onSelected: (Month) -> Unit = {}
    ) {
        Log.d("MonthPickerView", "Initialize called with preselectedIds: $preselectedIds")

        // Apply custom configuration
        this.config.apply(config)
        this.selectedMonthId = preselectedIds?.firstOrNull()
        this.onMonthSelected = onSelected

        // Re-apply configuration in case it was updated
        setupViews()

        viewScope.launch {
            Log.d("MonthPickerView", "Starting data fetch")
            // Force refresh to ensure data is loaded
            MonthStore.forceRefresh(monthRepository)

            MonthStore.state.collectLatest { state ->
                Log.d("MonthPickerView", "State received: $state")
                handleDataState(state)
            }
        }
    }

    private fun handleDataState(state: DataState<List<Month>>) {
        Log.d("MonthPickerView", "Handling state: $state")
        when (state) {
            is DataState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                emptyStateView.isVisible = false
                Log.d("MonthPickerView", "Loading state")
            }
            is DataState.Success -> {
                progressBar.isVisible = false
                val months = state.data ?: emptyList()
                Log.d("MonthPickerView", "Success state, months count: ${months.size}")

                if (months.isEmpty()) {
                    recyclerView.isVisible = false
                    emptyStateView.isVisible = true
                    Log.d("MonthPickerView", "No months available")
                } else {
                    recyclerView.isVisible = true
                    emptyStateView.isVisible = false
                    setUpAdapter(applySorting(applyFilter(months)))
                    adapter?.notifyDataSetChanged()
                }
            }
            is DataState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                emptyStateView.isVisible = true
                Log.e("MonthPickerView", "Error state: ${state.message}")
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("MonthPickerView", "Other state: $state")
            } // Handle Idle and Exception states
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
        Log.d("MonthPickerView", "Setting up adapter with ${months.size} months")
        adapter = MonthAdapter(
            months = months,
            selectedIds = if (config.multipleSelection)
                selectedMonthIds.toList()
            else listOfNotNull(selectedMonthId),
            config = config,
            onSelect = { selected ->
                Log.d("MonthPickerView", "Month selected: ${selected.name}")
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

            if (triggerCallback && monthId != null) {
                // Find the month object and trigger callback
                adapter?.getAllMonths()?.find { it.id == monthId }?.let { month ->
                    onMonthSelected?.invoke(month)
                }
            }
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

    private fun showCreateMonthDialog() {
        // Check if dialog already exists
        if (monthCreateDialog != null) {
            // If dialog still exists but is not showing, show it
            if (!monthCreateDialog!!.isShowing()) {
                monthCreateDialog!!.show()
            }
            // Dialog is already showing, no need to do anything
            return
        }

        // Create a new dialog only if one doesn't exist
        monthCreateDialog = GenericDialog.Builder(context)
            .setIcon(R.drawable.add)
            .setTitle("Create Month!")
            .setPositiveButton("Create", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    // Get form values
                    val name = genericDialog.findViewById<EditText>(R.id.ev_month_name)?.text.toString()
                    val typeRadioGroup = genericDialog.findViewById<RadioGroup>(R.id.rg_month_type)
                    val selectedTypeId = typeRadioGroup?.checkedRadioButtonId

                    // Determine month type
                    val type = when(selectedTypeId) {
                        R.id.rb_automatic -> "automatic"
                        R.id.rb_manual -> "manual"
                        else -> ""
                    }

                    // Get other values based on type
                    var month: Int? = null
                    var year: Int? = null
                    var startAt: String? = null
                    var forceCloseOther = false

                    if (type == "automatic") {
                        val monthSpinner = genericDialog.findViewById<Spinner>(R.id.spinner_month)
                        month = monthSpinner?.selectedItemPosition?.plus(1) // Adding 1 because position starts from 0

                        val yearSpinner = genericDialog.findViewById<Spinner>(R.id.spinner_year)
                        year = yearSpinner?.selectedItem?.toString()?.toIntOrNull()
                    } else if (type == "manual") {
                        startAt = genericDialog.findViewById<TextView>(R.id.tv_start_date_value)?.text.toString()
                    }

                    // Check force close checkbox
                    val forceCloseCheckbox = genericDialog.findViewById<CheckBox>(R.id.cb_force_close)
                    forceCloseOther = forceCloseCheckbox?.isChecked ?: false

                    // Call your API function to create month
                    createMonth(name, type, month, year, startAt, forceCloseOther)

                    // Dialog will be dismissed in the createMonth function if successful
                }
            })
            .setNegativeButton("Cancel", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    // Dismiss the dialog when cancel is clicked
                    genericDialog.dismiss()

                    // Clear the dialog reference
                    monthCreateDialog = null
                }
            })

            .setAutoDismiss(false)
            .setContentView(R.layout.layout_create_month)
            .build() // Use build instead of show to create the dialog

        // After dialog is created, set up the spinners and date picker
        setupFormElements(monthCreateDialog!!)

        // Show the dialog after setup
        monthCreateDialog!!.show()
    }

    private fun setupFormElements(dialog: GenericDialog) {
        // Set up Month Spinner
        val monthSpinner = dialog.findViewById<Spinner>(R.id.spinner_month)
        val months = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner?.adapter = monthAdapter

        // Set default to current month (current month index is 0-based)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        monthSpinner?.setSelection(currentMonth)

        // Set up Year Spinner
        val yearSpinner = dialog.findViewById<Spinner>(R.id.spinner_year)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear.toString()) // Only the current year as per validation
        val yearAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner?.adapter = yearAdapter

        // Set up Date Picker for Start Date
        val btnPickDate = dialog.findViewById<Button>(R.id.btn_pick_start_date)
        val tvStartDateValue = dialog.findViewById<TextView>(R.id.tv_start_date_value)

        // Set initial date
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        tvStartDateValue?.text = dateFormat.format(calendar.time)

        btnPickDate?.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(Calendar.YEAR, selectedYear)
                    calendar.set(Calendar.MONTH, selectedMonth)
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                    // Format the date as YYYY-MM-DD
                    tvStartDateValue?.text = dateFormat.format(calendar.time)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        // Set up visibility logic for form fields
        val typeRadioGroup = dialog.findViewById<RadioGroup>(R.id.rg_month_type)
        val automaticFields = dialog.findViewById<LinearLayout>(R.id.automatic_fields)
        val manualFields = dialog.findViewById<LinearLayout>(R.id.manual_fields)

        // Set default visibility
        automaticFields?.visibility = View.GONE
        manualFields?.visibility = View.GONE

        // Set listener to show/hide fields based on selected type
        typeRadioGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_automatic -> {
                    automaticFields?.visibility = View.VISIBLE
                    manualFields?.visibility = View.GONE
                }
                R.id.rb_manual -> {
                    automaticFields?.visibility = View.GONE
                    manualFields?.visibility = View.VISIBLE
                }
            }
        }
    }

    // Now implement createMonth with the stored repository
    private fun createMonth(
        name: String?,
        type: String,
        month: Int?,
        year: Int?,
        startAt: String?,
        forceCloseOther: Boolean
    ) {
        Log.d("MonthPickerView", "Creating month: name=$name, type=$type")

        // Validate inputs
        if (name.isNullOrBlank()) {
            Toast.makeText(context, "Month name is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (type == "automatic" && (month == null || year == null)) {
            Toast.makeText(context, "Month and year are required for automatic type", Toast.LENGTH_SHORT).show()
            return
        }

        if (type == "manual" && startAt.isNullOrEmpty()) {
            Toast.makeText(context, "Start date is required for manual type", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading indicator
        progressBar.isVisible = true

        viewScope.launch {
            try {
                // Make API call through repository
                val response = monthRepository.createMonth(
                    name = name,
                    type = type,
                    month = month,
                    year = year,
                    startAt = startAt,
                    forceCloseOther = forceCloseOther
                )

                // Process response
                if (response.isSuccessful && response.body()?.error != true) {
                    // Show success message
                    Toast.makeText(context, "Month created successfully", Toast.LENGTH_SHORT).show()

                    monthCreateDialog?.dismiss()
                    monthCreateDialog = null

                    // Refresh month list
                    MonthStore.forceRefresh(monthRepository)
                } else {
                    // Show error message
                    val errorMsg = response.body()?.msg ?: "Unknown error occurred"
                    Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e("MonthPickerView", "Error creating month", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Hide loading indicator
                progressBar.isVisible = false
            }
        }
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