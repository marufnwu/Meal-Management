package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.DepositListAdapter
import com.logicline.mydining.databinding.ActivityDepositBinding
import com.logicline.mydining.models.Deposit
import com.logicline.mydining.models.response.DepositsResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepositActivity : AppCompatActivity(), MyDatePicker.OnDateSelectListener {
    lateinit var myFullScreenAd: MyFullScreenAd
    lateinit var binding : ActivityDepositBinding
    lateinit var month : String
    lateinit var year : String
    lateinit var loadingDialog: LoadingDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myFullScreenAd = MyFullScreenAd(this, true)

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


    }

    override fun onStart() {
        super.onStart()
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

        adpter.onItemClickListener = (object : DepositListAdapter.OnItemClickListener {
            override fun onClick(userId: String, messId:String) {
                showUserDepositHistory(userId, messId)
            }

        })
        binding.recyDeposit.adapter = adpter
    }

    private fun showUserDepositHistory(userId: String, messId: String) {
        startActivity(Intent(this, DepositHistoryActivity::class.java)
            .putExtra(Constant.YEAR, year).putExtra(Constant.MONTH, month)
            .putExtra(Constant.USER_ID,userId)
            .putExtra(Constant.MESS_ID, messId)
            .putExtra(Constant.HISTORY_TYPE, DepositHistoryActivity.Type.SINGLE_USER.name))
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

    override fun onBackPressed() {
        myFullScreenAd.showAd()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }
}