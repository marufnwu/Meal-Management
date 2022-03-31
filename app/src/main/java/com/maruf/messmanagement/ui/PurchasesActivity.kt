package com.maruf.messmanagement.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maruf.messmanagement.R
import com.maruf.messmanagement.adapter.PurchaseListAdapter
import com.maruf.messmanagement.databinding.ActivityPurchasesBinding
import com.maruf.messmanagement.models.response.PurchaseListResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PurchasesActivity : AppCompatActivity() {
    lateinit var binding : ActivityPurchasesBinding
    lateinit var month : String
    lateinit var year : String
    lateinit var loadingDialog: LoadingDialog
    var type = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)

        type = intent.getIntExtra(Constant.PURCHASE_TYPE, 0)

        if(type==1){
            binding.btnAddPurchase.text = "Add Purchase"
        }else{
            binding.btnAddPurchase.text = "Add Others Cost"

        }

        if(!Constant.isManagerOrSuperUser()) binding.btnAddPurchase.visibility = View.GONE

        binding.recyPurchase.setHasFixedSize(true)
        binding.recyPurchase.layoutManager = LinearLayoutManager(this)


        binding.txtMonthYear.text = Constant.getCurrentMonthName()+" "+Constant.getCurrentYear()

        month = Constant.getCurrentMonthNumber()
        year = Constant.getCurrentYear()

        binding.txtMonthYear.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddPurchase.setOnClickListener {
            startActivity(Intent(this, AddPurchaseActivity::class.java).putExtra(Constant.PURCHASE_TYPE, type))
        }

        getPurchaseList()
    }

    fun getPurchaseList(){
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getPurchasetByDate(year, month, type)
            .enqueue(object: Callback<PurchaseListResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<PurchaseListResponse>, response: Response<PurchaseListResponse>) {
                    loadingDialog.hide()

                    if(response.isSuccessful && response.body()!=null){
                        val purchaseListResponse = response.body()!!
                        if(!purchaseListResponse.error){

                            var purchaseType = ""
                            if(type==1){
                                purchaseType = "Total Purchase"
                            }else{
                                purchaseType = "Total Others Cost"
                            }
                            binding.txtTotalPurchase.text = purchaseType+" "+purchaseListResponse.totalPurchase

                            purchaseListResponse.purchases.let {purchaseList->
                                val adpter  = PurchaseListAdapter(this@PurchasesActivity, purchaseList)
                                binding.recyPurchase.adapter = adpter
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PurchaseListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    @SuppressLint("SetTextI18n")
    public fun showDateTimePicker() {
        // Get Current Date
        // Get Current Date
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            this.year = Constant.getYear(date)
            this.month = Constant.getMonthNumber(date)

            binding.txtMonthYear.text = Constant.getMonthNumber(date)+" "+Constant.getYear(date)
            binding.txtMonthYear.text = Constant.getMonthName(date)+" "+Constant.getYear(date)

            getPurchaseList()

        }, mYear, mMonth, mDay)

        datePickerDialog.show()

    }
}