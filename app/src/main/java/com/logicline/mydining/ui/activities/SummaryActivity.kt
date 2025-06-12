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
import com.logicline.mydining.utils.MyDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.logicline.mydining.databinding.ActivitySummaryBinding

class SummaryActivity : BaseActivity() {
    private lateinit var myFullScreenAd: MyFullScreenAd
    private lateinit var binding: ActivitySummaryBinding
    private var month: String = Constant.getCurrentMonthNumber()
    private var year: String = Constant.getCurrentYear()
    private lateinit var loadingDialog: LoadingDialog
    private var userSummary: List<UserSummaryItem> = arrayListOf()
    private val decimalFormat = DecimalFormat("#,##0.00")

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
        }

        setupUI()
        getSummary()
    }

    private fun setupUI() {
        // Setup month-year chip with click listener for date selection
        //binding.chipMonthYear.text = "${Constant.getMonthName(month)} $year"
        binding.chipMonthYear.setOnClickListener {

        }

        // Setup RecyclerView for user summary
        binding.recySummary.setHasFixedSize(true)
        binding.recySummary.layoutManager = LinearLayoutManager(this)
    }


    private fun getSummary() {
        setToDefault()
        loadingDialog.show()

        (application as MyApplication)
            .myApi
            .getMonthSummary()
            .enqueue(object : Callback<ServerResponse<MonthSummary>> {
                override fun onResponse(
                    call: Call<ServerResponse<MonthSummary>>,
                    response: Response<ServerResponse<MonthSummary>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body() != null) {
                        val summaryResponse = response.body()!!
                        if (!summaryResponse.error && summaryResponse.data != null) {
                            setData(summaryResponse.data!!)
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<MonthSummary>>, t: Throwable) {
                    loadingDialog.hide()
                }
            })
    }

    private fun setData(monthSummary: MonthSummary) {
        val summary = monthSummary.summary
        val details = monthSummary.details

        // Set month name in title if available
        monthSummary.month?.name?.let { monthName ->
            binding.chipMonthYear.text = "$monthName $year"
        }

        // Set financial data
        binding.tvTotalPurchase.text = formatCurrency(summary.totalPurchase?.toFloat() ?: 0f)
        binding.totalDeposit.text = formatCurrency(summary.totalDeposit ?: 0f)

        // Set meal data
        binding.totalMeal.text = summary.totalMeal.toString()
        binding.mealRate.text = formatCurrency(summary.mealRate)

        // Set meal breakdown data
        details?.mealSummary?.let { mealSummary ->
            binding.chipBreakfastTotal.text = "Breakfast: ${mealSummary.breakfast}"
            binding.chipLunchTotal.text = "Lunch: ${mealSummary.lunch}"
            binding.chipDinnerTotal.text = "Dinner: ${mealSummary.dinner}"
        }

        // Set other cost data
        binding.otherCost.text = formatCurrency(summary.totalOtherCost ?: 0f)
        binding.chipCostPercentage.text = "৳${summary.otherCostShare}/user"

        // Calculate meal cost (totalMeal * mealRate)
        val totalMealCost = summary.totalMeal * summary.mealRate
        binding.totalMealCost.text = formatCurrency(totalMealCost)
        binding.mealCharge.text = formatCurrency(totalMealCost)

        // Calculate and set reserved amount and status
        val totalDeposit = summary.totalDeposit ?: 0f
        val inReserved = totalDeposit - summary.totalCost
        binding.inReserved.text = formatCurrency(inReserved)
        setBalanceStatus(inReserved, summary.status)

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
        // Reset financial data
        binding.tvTotalPurchase.text = formatCurrency(0f)
        binding.totalDeposit.text = formatCurrency(0f)
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

    @SuppressLint("SetTextI18n")
    private fun setDate(year: String, month: String) {
        this.month = month
        this.year = year
        getSummary()
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