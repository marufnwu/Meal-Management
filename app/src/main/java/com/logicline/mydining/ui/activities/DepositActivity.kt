package com.logicline.mydining.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.DepositListAdapter
import com.logicline.mydining.databinding.ActivityDepositBinding
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.models.DepositSum
import com.logicline.mydining.data.models.response.DepositsSumResponse
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.MyApplication
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepositActivity : BaseActivity() {
    lateinit var myFullScreenAd: MyFullScreenAd
    lateinit var binding: ActivityDepositBinding

    private var month: String = Constant.getCurrentMonthNumber()
    private var year: String = Constant.getCurrentYear()

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

        if (!AppPrefs.messUser?.hasAnyPermission(
                MessPermission.DEPOSIT_MANAGEMENT, MessPermission.DEPOSIT_ADD
            )!!
        ) binding.btnAddDeposit.visibility = View.GONE

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
            .enqueue(object : Callback<ServerResponse<DepositsSumResponse>> {
                override fun onResponse(
                    call: Call<ServerResponse<DepositsSumResponse>>,
                    response: Response<ServerResponse<DepositsSumResponse>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body() != null) {
                        val depositsResponse = response.body()!!
                        if (!depositsResponse.error) {
                            depositsResponse.data.let { data ->
                                binding.txtPageTitle.text =
                                    "Total Deposit ".plus(data?.totalDeposit)
                                setDepositsToRecyclerView(data?.deposits!!)
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<DepositsSumResponse>>,
                    t: Throwable
                ) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun setDepositsToRecyclerView(listOfDeposit: List<DepositSum>) {
        val adpter = DepositListAdapter(this, listOfDeposit.toMutableList())

        adpter.onItemClickListener = (object : DepositListAdapter.OnItemClickListener {
            override fun onClick(depositSum: DepositSum) {
                showUserDepositHistory(depositSum)
            }

        })
        binding.recyDeposit.adapter = adpter
    }

    private fun showUserDepositHistory(depositSum: DepositSum) {
        startActivity(
            Intent(this, DepositHistoryActivity::class.java)
                .putExtra(Constant.MESS_USER_ID, depositSum.messUserId)
                .putExtra(Constant.HISTORY_TYPE, DepositHistoryActivity.Type.SINGLE_USER.name)
        )
    }


    @SuppressLint("SetTextI18n")
    private fun setDate(year: String, month: String) {
        this.year = year
        this.month = month
        getDeposits()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        myFullScreenAd.showAd()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }
}