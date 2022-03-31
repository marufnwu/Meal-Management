package com.maruf.messmanagement.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.maruf.messmanagement.adapter.UserSummaryAdapter
import com.maruf.messmanagement.databinding.ActivitySummaryBinding
import com.maruf.messmanagement.models.response.MonthlySummaryResponse
import com.maruf.messmanagement.models.response.UsersSummary
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import com.maruf.messmanagement.utils.MyDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SummaryActivity : AppCompatActivity(), MyDatePicker.OnDateSelectListener {
    lateinit var binding : ActivitySummaryBinding
    lateinit var month : String
    lateinit var year : String
    lateinit var loadingDialog: LoadingDialog
    var userSummary : List<UsersSummary> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        setDate(Constant.getCurrentDate())

        binding.txtMonthYear.setOnClickListener {
            showDateTimePicker()
        }

        binding.recySummary.setHasFixedSize(true)
        binding.recySummary.layoutManager = LinearLayoutManager(this)

        getSummary()

    }

    fun getSummary(){
        setToDeafult()

        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getMonthSummary(year, month)
            .enqueue(object: Callback<MonthlySummaryResponse> {
                override fun onResponse(
                    call: Call<MonthlySummaryResponse>,
                    response: Response<MonthlySummaryResponse>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        val summaryResponse = response.body()!!
                        setData(summaryResponse)
                    }
                }

                override fun onFailure(call: Call<MonthlySummaryResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setData(monthlySummaryResponse: MonthlySummaryResponse) {
        binding.totalPurchase.text = monthlySummaryResponse.totalPurchase.toString()
        binding.totalMeal.text = monthlySummaryResponse.totalMeal.toString()
        binding.totalCost.text = monthlySummaryResponse.totalCost.toString()
        binding.mealCharge.text = monthlySummaryResponse.mealCharge.toString()
        binding.otherCost.text = monthlySummaryResponse.totalOtherCost.toString()
        binding.inReserved.text = monthlySummaryResponse.inReserve.toString()
        binding.totalDeposit.text = monthlySummaryResponse.totalDeposit.toString()
        binding.totalMealCost.text = monthlySummaryResponse.totalMealCost.toString()

        setRecyclear(monthlySummaryResponse.usersSummary)
    }

    fun setRecyclear(usersSummary: List<UsersSummary>) {
        val adapter = UserSummaryAdapter(this, usersSummary)
        binding.recySummary.adapter = adapter
    }

    private fun setToDeafult() {
        binding.totalPurchase.text = "00"
        binding.totalMeal.text = "00"
        binding.totalCost.text = "00"
        binding.mealCharge.text = "00"
        binding.otherCost.text = "00"
        binding.inReserved.text = "00"
        binding.totalDeposit.text = "00"
        binding.totalMealCost.text = "00"

        setRecyclear(listOf<UsersSummary>())

    }

    @SuppressLint("SetTextI18n")
    fun showDateTimePicker() {
        MyDatePicker(this, this)
            .create()
            .show()

    }

    @SuppressLint("SetTextI18n")
    private fun setDate(date: String) {
        month = Constant.getMonthNumber(date)
        year = Constant.getYear(date)
        binding.txtMonthYear.text = Constant.getYear(date)+" "+Constant.getMonthName(date)
        getSummary()
    }

    override fun date(date: Int, month: Int, year: Int) {
    }

    override fun dateString(date: String) {
        setDate(date)
    }
}