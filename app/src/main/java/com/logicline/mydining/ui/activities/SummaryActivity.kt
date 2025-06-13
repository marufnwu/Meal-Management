package com.logicline.mydining.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.UserSummaryAdapter
import com.logicline.mydining.data.models.MonthSummary
import com.logicline.mydining.data.models.UserSummaryItem
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.MyApplication
import com.logicline.mydining.data.models.response.ServerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.logicline.mydining.databinding.ActivitySummaryBinding
import com.logicline.mydining.ui.custom.StatusView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import com.logicline.mydining.ui.custom.monthpicker.MonthPickerDialog

class SummaryActivity : BaseActivity() {
    private lateinit var myFullScreenAd: MyFullScreenAd
    private lateinit var binding: ActivitySummaryBinding
    private var month: String = Constant.getCurrentMonthNumber()
    private var year: String = Constant.getCurrentYear()
    private lateinit var loadingDialog: LoadingDialog
    private var userSummary: List<UserSummaryItem> = arrayListOf()
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var currentMonthApiId: Int? = null // To store the ID of the current month from API
    private var currentMessId: Int? = null // To store the mess_id if needed for API calls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        myFullScreenAd = MyFullScreenAd(this, true)
        loadingDialog = LoadingDialog(this)

        intent?.let {
            it.getStringExtra(Constant.YEAR)?.let { yearValue ->
                year = yearValue
            }

            it.getStringExtra(Constant.MONTH)?.let { monthValue ->
                month = monthValue
            }
            // Potentially load mess_id if it comes from intent and is needed globally
            // currentMessId = it.getIntExtra(Constant.MESS_ID, -1).takeIf { id -> id != -1 }
        }

        setupUI()
        getSummary()
    }

    private fun showMonthPicker(){
        MonthPickerDialog.show(
            context = this@SummaryActivity,
            preselectedMonthId = currentMonthApiId,
            onMonthSelected = { selectedMonthData -> // selectedMonthData is of type com.logicline.mydining.data.models.Month
                selectedMonthData.startAt.let { startAtString ->
                    try {
                        val parsedDate = inputDateFormat.parse(startAtString)
                        if (parsedDate != null) {
                            val cal = Calendar.getInstance()
                            cal.time = parsedDate
                            val newYear = cal.get(Calendar.YEAR).toString()
                            val newMonth = (cal.get(Calendar.MONTH) + 1).toString() // Calendar.MONTH is 0-indexed
                            setDate(newYear, newMonth)
                        } else {
                            // Log error or show a toast if date parsing fails
                            android.util.Log.e("SummaryActivity", "Failed to parse date from selected month: $startAtString")
                        }
                    } catch (e: java.text.ParseException) {
                        android.util.Log.e("SummaryActivity", "ParseException for date: $startAtString", e)
                        // Optionally, show a toast to the user
                    }

                    getSummary(selectedMonthData.id)
                }
            }
        )
    }

    private fun setupUI() {
        // Setup month-year chip with click listener for date selection
        binding.chipMonthYear.setOnClickListener {
            showMonthPicker()
        }

        // Setup RecyclerView for user summary
        binding.recySummary.setHasFixedSize(true)
        binding.recySummary.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Shows an error or info state in the StatusView
     */
    private fun showStatusMessage(
        type: StatusView.StatusType,
        message: String,
        primaryButtonText: String = "Select Month",
        secondaryButtonText: String = "Back",
        primaryAction: () -> Unit = { showMonthPicker() },
        secondaryAction: () -> Unit = { onBackPressed() }
    ) {
        binding.statusView
            .setStatusViewVisible(true)
            .setStatus(type, message)
            .setPositiveButton(primaryButtonText) { primaryAction() }
            .setNegativeButton(secondaryButtonText) { secondaryAction() }
    }

    private fun getSummary(monthId : Int? = null) {
        setToDefault()
        loadingDialog.show()

        // API interface doesn't accept year and month parameters
        (application as MyApplication)
            .myApi
            .getMonthSummary(monthId)
            .enqueue(object : Callback<ServerResponse<MonthSummary>> {
                override fun onResponse(
                    call: Call<ServerResponse<MonthSummary>>,
                    response: Response<ServerResponse<MonthSummary>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body() != null) {
                        val summaryResponse = response.body()!!
                        if (!summaryResponse.error && summaryResponse.data != null) {
                            // Store mess_id from the response if it's part of monthInfo and needed
                            // summaryResponse.data!!.month?.mess_id?.let { currentMessId = it }
                            setData(summaryResponse.data!!)
                            binding.statusView.hideStatusView()
                        } else {
                            // API returned an error in the response
                            showStatusMessage(
                                type = StatusView.StatusType.INFO,
                                message = "No summary data available for the selected month."
                            )
                        }
                    } else {
                        // Handle API error response
                        val errorCode = response.code()
                        val errorMessage = when (errorCode) {
                            404 -> "No data found for the selected month."
                            401, 403 -> "You don't have access to this information."
                            500 -> "Server error. Please try again later."
                            else -> "Couldn't retrieve summary data. Please try again."
                        }

                        showStatusMessage(
                            type = StatusView.StatusType.ERROR,
                            message = errorMessage
                        )

                        val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                        android.util.Log.e("SummaryActivity", "API Error: ${response.code()} - $errorBody")
                    }
                }

                override fun onFailure(call: Call<ServerResponse<MonthSummary>>, t: Throwable) {
                    loadingDialog.hide()
                    android.util.Log.e("SummaryActivity", "API Call Failure: ", t)

                    val errorMessage = when {
                        t.message?.contains("timeout", ignoreCase = true) == true -> "Connection timed out. Please check your internet connection."
                        t.message?.contains("Unable to resolve host", ignoreCase = true) == true -> "No internet connection. Please check your network settings."
                        else -> "Something went wrong. Please try again."
                    }

                    showStatusMessage(
                        type = StatusView.StatusType.ERROR,
                        message = errorMessage,
                        primaryButtonText = "Retry",
                        primaryAction = { getSummary(monthId) }
                    )
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun setData(monthSummary: MonthSummary) {
        val summary = monthSummary.summary
        val details = monthSummary.details
        val monthInfo = monthSummary.month

        // Store the current month's API ID for picker preselection
        currentMonthApiId = monthInfo.id

        // Set month information
        monthInfo.let { info ->
            val monthDisplayName = info.name
            val yearDisplayName = info.startAt.let { startDateString ->
                try {
                    inputDateFormat.parse(startDateString)?.let { parsedDate ->
                        // Use SimpleDateFormat to format just the year
                        SimpleDateFormat("yyyy", Locale.getDefault()).format(parsedDate)
                    } ?: year // Fallback to activity's year if parsing returns null
                } catch (e: Exception) { // Catch all exceptions for date parsing
                    android.util.Log.e("SummaryActivity", "Error parsing startAt date: $startDateString", e)
                    year // Fallback to activity's year on any exception
                }
            }

            binding.chipMonthYear.text = "$monthDisplayName $yearDisplayName"

            // Update the status label
            binding.tvMonthStatus.text = if (info.isActive) "Active" else "Inactive"

            // Format and set the period dates
            val startDate = info.startAt.let { dateString ->
                try {
                    inputDateFormat.parse(dateString)?.let { parsedDate -> outputDateFormat.format(parsedDate) }
                } catch (e: Exception) {
                    android.util.Log.e("SummaryActivity", "Error formatting start date", e)
                    null
                }
            } ?: "N/A"

            val endDate = info.endAt?.let { dateString ->
                try {
                    inputDateFormat.parse(dateString)?.let { parsedDate -> outputDateFormat.format(parsedDate) }
                } catch (e: Exception) {
                    android.util.Log.e("SummaryActivity", "Error formatting end date", e)
                    null
                }
            } ?: "Ongoing"

            binding.tvMonthPeriod.text = "$startDate - $endDate"

            // Set the newly added fields
            binding.tvMonthType.text = info.type.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.tvMessId.text = info.messId.toString()
        }

        // Set financial data - safely handle different potential types in the API response
        try {
            // Using the correct property names from SummaryData class
            val totalPurchase = summary.totalPurchase?.toFloat() ?: 0f
            binding.tvTotalPurchase.text = formatCurrency(totalPurchase)

            val totalDeposit = summary.totalDeposit ?: 0f
            binding.totalDeposit.text = formatCurrency(totalDeposit)

            val totalCost = summary.totalCost
            binding.tvTotalOverallCost.text = formatCurrency(totalCost)

            // Set meal data
            val totalMealValue = summary.totalMeal
            binding.totalMeal.text = totalMealValue.toString()

            val mealRate = summary.mealRate
            binding.mealRate.text = formatCurrency(mealRate)

            // Set meal breakdown data
            details?.mealSummary?.let { mealSummary ->
                binding.chipBreakfastTotal.text = "Breakfast: ${mealSummary.breakfast}"
                binding.chipLunchTotal.text = "Lunch: ${mealSummary.lunch}"
                binding.chipDinnerTotal.text = "Dinner: ${mealSummary.dinner}"
            }

            // Set other cost data
            val otherCost = summary.totalOtherCost ?: 0f
            binding.otherCost.text = formatCurrency(otherCost)
            binding.chipCostPercentage.text = "৳${summary.otherCostShare ?: 0}/user"

            // Calculate meal cost (totalMeal * mealRate)
            val totalMealCost = totalMealValue * mealRate
            binding.totalMealCost.text = formatCurrency(totalMealCost)
            binding.mealCharge.text = formatCurrency(totalMealCost) // Assuming meal charge is same as total meal cost

            // Calculate and set reserved amount and status
            val inReserved = totalDeposit - totalCost
            binding.inReserved.text = formatCurrency(inReserved)
            setBalanceStatus(inReserved, summary.status)
        } catch (e: Exception) {
            android.util.Log.e("SummaryActivity", "Error processing numerical data", e)
            // In case of any exception during calculations, reset to safe defaults
            binding.totalMeal.text = "0"
            binding.mealRate.text = formatCurrency(0f)
            binding.totalMealCost.text = formatCurrency(0f)
            binding.mealCharge.text = formatCurrency(0f)
            binding.inReserved.text = formatCurrency(0f)
        }

        // Set users count
        val userCount = details?.users?.size ?: 0
        binding.chipUsersCount.text = "$userCount ${if (userCount == 1) "User" else "Users"}"

        // Set users summary
        details?.users?.let { users ->
            setRecycler(users)
        } ?: setRecycler(emptyList())
    }

    private fun setBalanceStatus(balance: Float, status: String?) {
        val isDeficit = balance < 0 || status == "deficit"

        binding.chipBalanceStatus.apply {
            text = if (isDeficit) "Deficit" else "Positive"
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@SummaryActivity,
                    if (isDeficit) R.color.red_700 else R.color.green_700
                )
            )
            setTextColor(Color.WHITE)
        }
    }

    private fun setRecycler(usersSummary: List<UserSummaryItem>) {
        val adapter = UserSummaryAdapter(this, usersSummary)
        binding.recySummary.adapter = adapter
    }

    private fun setToDefault() {
        // Reset month info
        binding.chipMonthYear.text = "Month Year"
        binding.tvMonthStatus.text = "Active" // Default status
        binding.tvMonthPeriod.text = "- - -" // Default period
        binding.tvMonthType.text = "Manual" // Default type
        binding.tvMessId.text = "-" // Default mess ID
        currentMonthApiId = null // Reset current month ID

        // Reset financial data
        binding.tvTotalPurchase.text = formatCurrency(0f)
        binding.totalDeposit.text = formatCurrency(0f)
        binding.tvTotalOverallCost.text = formatCurrency(0f)
        binding.inReserved.text = formatCurrency(0f)

        // Reset meal data
        binding.totalMeal.text = "0"
        binding.mealRate.text = formatCurrency(0f)
        binding.totalMealCost.text = formatCurrency(0f)
        binding.mealCharge.text = formatCurrency(0f)

        // Reset meal breakdown
        binding.chipBreakfastTotal.text = "Breakfast: 0"
        binding.chipLunchTotal.text = "Lunch: 0"
        binding.chipDinnerTotal.text = "Dinner: 0"

        // Reset other cost
        binding.otherCost.text = formatCurrency(0f)
        binding.chipCostPercentage.text = "৳0/user"

        // Reset users count
        binding.chipUsersCount.text = "0 Users"

        // Reset balance status
        binding.chipBalanceStatus.apply {
            text = "Balanced"
            chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(this@SummaryActivity, R.color.gray_500)
            )
            setTextColor(Color.WHITE)
        }

        setRecycler(emptyList())
    }

    private fun formatCurrency(amount: Float): String {
        return "৳${decimalFormat.format(amount)}"
    }

    private fun formatCurrency(amount: Double): String {
        return "৳${decimalFormat.format(amount)}"
    }

    @SuppressLint("SetTextI18n")
    private fun setDate(year: String, month: String) {
        this.month = month
        this.year = year
    }

    override fun onBackPressed() {
        myFullScreenAd.showAd()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }
}