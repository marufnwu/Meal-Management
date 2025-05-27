package com.logicline.mydining.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.UserSummaryAdapter
import com.logicline.mydining.databinding.ActivitySummaryBinding
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

//        binding.recySummary.setHasFixedSize(true)
//        binding.recySummary.layoutManager = LinearLayoutManager(this)

        getSummary()
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

        // Set financial data
        binding.tvTotalPurchase.text = formatCurrency(summary.totalPurchase ?: 0f)
        binding.tvTotalDeposits.text = formatCurrency(summary.totalDeposit ?: 0f)

        // Set meal data
        binding.tvTotalMealsValue.text = decimalFormat.format(summary.totalMeal)
        binding.tvMealRateValue.text = formatCurrency(summary.mealCharge ?: 0f)

        // Set cost data
//        binding.totalCost.text = formatCurrency(summary.totalCost)
        binding.tvTotalOtherCost.text = formatCurrency(summary.totalOtherCost ?: 0f)

        // Calculate meal cost (totalMeal * mealRate)
        val totalMealCost = summary.totalMeal * summary.mealRate
//        binding.totalMealCost.text = formatCurrency(totalMealCost)

        // Calculate reserved amount (totalDeposit - totalCost)
        val totalDeposit = summary.totalDeposit ?: 0f
        val inReserved = totalDeposit - summary.totalCost
        binding.tvMessNetBalance.text = summary.status+" "+formatCurrency(inReserved)

        // Handle fund visibility (if needed)
        // Uncomment and adjust based on your requirements
        /*
        if (LocalDB.getInitialData()?.messData?.fundStatus!! > 0) {
            binding.layoutFund.visibility = View.VISIBLE
            binding.totalFund.text = formatCurrency(summary.totalFund ?: 0f)
        } else {
            binding.layoutFund.visibility = View.GONE
        }
        */

        // Set users summary
        monthSummary.details?.users?.let { users ->
            setRecycler(users)
        } ?: setRecycler(emptyList())
    }

    private fun setRecycler(usersSummary: List<UserSummaryItem>) {
        val adapter = UserSummaryAdapter(this, usersSummary)
//        binding.recySummary.adapter = adapter
    }

    private fun setToDefault() {
        binding.tvTotalPurchase.text = formatCurrency(0f)
        binding.tvTotalMealsValue.text = "0"
//        binding.totalCost.text = formatCurrency(0f)
        binding.tvMealRateValue.text = formatCurrency(0f)
        binding.tvTotalOtherCost.text = formatCurrency(0f)
//        binding.inReserved.text = formatCurrency(0f)
        binding.tvTotalDeposits.text = formatCurrency(0f)
//        binding.totalMealCost.text = formatCurrency(0f)

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