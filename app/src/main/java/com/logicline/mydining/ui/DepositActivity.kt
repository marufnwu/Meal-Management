package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.DepositListAdapter
import com.logicline.mydining.databinding.ActivityDepositBinding
import com.logicline.mydining.models.DepositSum
import com.logicline.mydining.models.response.DepositsSumResponse
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepositActivity : BaseActivity() {
    lateinit var myFullScreenAd: MyFullScreenAd
    lateinit var binding : ActivityDepositBinding

    private var month : String = Constant.getCurrentMonthNumber()
    private var year : String = Constant.getCurrentYear()

    lateinit var loadingDialog: LoadingDialog

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.deposit)



        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog = LoadingDialog(this)

        if(!Constant.isManagerOrSuperUser()) binding.btnAddDeposit.visibility = View.GONE

        intent?.let {
            it.getStringExtra(Constant.YEAR)?.let {
                year = it
            }

            it.getStringExtra(Constant.MONTH)?.let {
                month = it

            }
        }



        binding.recyDeposit.setHasFixedSize(true)
        binding.recyDeposit.layoutManager = LinearLayoutManager(this)


        binding.btnAddDeposit.setOnClickListener {
            startActivity(Intent(this, AddDepositActivity::class.java))
        }

        binding.monthPicker
            .builder(null, mYear = year.toInt(), mMonth = month.toInt(), mDay = 1  ).onDateSelectListener = object : MyDatePicker.OnDateSelectListener {
            override fun date(date: Int, month: Int, year: Int) {
                setDate(year.toString(), month.toString())
            }

            override fun dateString(date: String) {

            }

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
            .getDeposit()
            .enqueue(object: Callback<ServerResponse<DepositsSumResponse>> {
                override fun onResponse(
                    call: Call<ServerResponse<DepositsSumResponse>>,
                    response: Response<ServerResponse<DepositsSumResponse>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        val depositsResponse = response.body()!!
                        if(!depositsResponse.error){
                            depositsResponse.data.let { data->
                                binding.txtPageTitle.text = "Total Deposit ".plus(data?.totalDeposit)
                                setDepositsToRecyclerView(data?.deposits!!)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<DepositsSumResponse>>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun setDepositsToRecyclerView(listOfDeposit: List<DepositSum>) {
        val adpter = DepositListAdapter(this, listOfDeposit.toMutableList())

        adpter.onItemClickListener = (object : DepositListAdapter.OnItemClickListener {
            override fun onClick(userId: Int, ) {
                showUserDepositHistory(userId)
            }

        })
        binding.recyDeposit.adapter = adpter
    }

    private fun showUserDepositHistory(userId: Int) {
        startActivity(Intent(this, DepositHistoryActivity::class.java)
            .putExtra(Constant.YEAR, year).putExtra(Constant.MONTH, month)
            .putExtra(Constant.USER_ID,userId)
            .putExtra(Constant.MESS_ID, 0)
            .putExtra(Constant.HISTORY_TYPE, DepositHistoryActivity.Type.SINGLE_USER.name))
    }


    @SuppressLint("SetTextI18n")
    private fun setDate(year:String, month:String) {
        this.year = year
        this.month = month
        getDeposits()
    }


    override fun onBackPressed() {
        super.onBackPressed()
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