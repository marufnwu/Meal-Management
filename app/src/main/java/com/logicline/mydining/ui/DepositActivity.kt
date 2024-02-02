package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.DepositListAdapter
import com.logicline.mydining.databinding.ActivityDepositBinding
import com.logicline.mydining.models.Deposit
import com.logicline.mydining.models.response.DepositsResponse
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
            .builder(null, mYear = year?.toInt(), mMonth = month?.toInt(), mDay = 1  )
            .onDateSelectListener = object : MyDatePicker.OnDateSelectListener {
            override fun date(date: Int?, month: Int?, year: Int?) {
                setDate(year.toString(), month.toString())
            }

            override fun dateString(date: String) {

            }

            override fun month(monthId: Int) {
                getDeposits(monthId)
            }

        }


    }

    override fun onStart() {
        super.onStart()
        getDeposits()

    }

    private fun getDeposits(monthId: Int? =null) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getDeposit(year, month, monthId)
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
    private fun setDate(year:String, month:String) {
        this.year = year
        this.month = month
        getDeposits()
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