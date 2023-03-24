package com.logicline.mydining.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.UserSummaryAdapter
import com.logicline.mydining.databinding.ActivitySummaryBinding
import com.logicline.mydining.models.response.MonthlySummaryResponse
import com.logicline.mydining.models.response.UsersSummary
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SummaryActivity : AppCompatActivity(), MyDatePicker.OnDateSelectListener {
    private lateinit var myFullScreenAd: MyFullScreenAd
    lateinit var binding : ActivitySummaryBinding
    lateinit var month : String
    lateinit var year : String
    lateinit var loadingDialog: LoadingDialog
    var userSummary : List<UsersSummary> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)
        myFullScreenAd = MyFullScreenAd(this, true)
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
        binding.totalPurchase.text = monthlySummaryResponse.totalPurchase
        binding.totalMeal.text = monthlySummaryResponse.totalMeal
        binding.totalCost.text = monthlySummaryResponse.totalCost
        binding.mealCharge.text = monthlySummaryResponse.mealCharge
        binding.otherCost.text = monthlySummaryResponse.totalOtherCost
        binding.inReserved.text = monthlySummaryResponse.inReserve
        binding.totalDeposit.text = monthlySummaryResponse.totalDeposit
        binding.totalMealCost.text = monthlySummaryResponse.totalMealCost

        if(LocalDB.getInitialData()?.messData?.fundStatus!!>0){
            binding.layoutFund.visibility  =View.VISIBLE
            binding.totalFund.text = monthlySummaryResponse.totalFund
        }else{
            binding.layoutFund.visibility  =View.GONE

        }

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

    override fun onBackPressed() {
        myFullScreenAd.showAd()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return false
    }
}