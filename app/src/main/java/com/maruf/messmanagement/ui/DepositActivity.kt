package com.maruf.messmanagement.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maruf.messmanagement.R
import com.maruf.messmanagement.adapter.DepositListAdapter
import com.maruf.messmanagement.databinding.ActivityDepositBinding
import com.maruf.messmanagement.models.response.Deposit
import com.maruf.messmanagement.models.response.DepositsResponse
import com.maruf.messmanagement.models.response.GenericRespose
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import com.maruf.messmanagement.utils.MyDatePicker
import com.maruf.messmanagement.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepositActivity : AppCompatActivity(), MyDatePicker.OnDateSelectListener {
    lateinit var binding : ActivityDepositBinding
    lateinit var month : String
    lateinit var year : String
    lateinit var loadingDialog: LoadingDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)

        if(!Constant.isManagerOrSuperUser()) binding.btnAddDeposit.visibility = View.GONE
        setDate(Constant.getCurrentDate())



        binding.recyDeposit.setHasFixedSize(true)
        binding.recyDeposit.layoutManager = LinearLayoutManager(this)

        binding.txtMonthYear.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddDeposit.setOnClickListener {
            startActivity(Intent(this, AddDepositActivity::class.java))
        }

        getDeposits()

    }

    private fun getDeposits() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getDeposit(year, month)
            .enqueue(object: Callback<DepositsResponse> {
                override fun onResponse(
                    call: Call<DepositsResponse>,
                    response: Response<DepositsResponse>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        val depositsResponse = response.body()!!
                        if(!depositsResponse.error){
                            depositsResponse.deposits.let { listOfDeposit->
                                binding.txtPageTitle.text = "Total Deposit ".plus(depositsResponse.totalDeposit)
                                setDepositsToRecyclerView(listOfDeposit)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<DepositsResponse>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun setDepositsToRecyclerView(listOfDeposit: List<Deposit>) {
        val adpter = DepositListAdapter(this, listOfDeposit.toMutableList())
        binding.recyDeposit.adapter = adpter
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
        getDeposits()
    }

    override fun date(date: Int, month: Int, year: Int) {
    }

    override fun dateString(date: String) {
        setDate(date)
    }
}