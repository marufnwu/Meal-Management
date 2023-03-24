package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.FundAdapter
import com.logicline.mydining.databinding.ActivityFundBinding
import com.logicline.mydining.databinding.DialogEditPurchaseLayoutBinding
import com.logicline.mydining.models.Fund
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FundActivity : BaseActivity(false) {
    lateinit var adapter: FundAdapter
    private var funds : MutableList<Fund> = mutableListOf()
    lateinit var binding : ActivityFundBinding
    lateinit var loadingDialog: LoadingDialog

    lateinit var month : String
    lateinit var year : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFundBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Added Funds"

        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        initViews()

        setDate(Constant.getCurrentDate())
    }

    private fun getFunds(){
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getFunds(year, month)
            .enqueue(object: Callback<ServerResponse<MutableList<Fund>>> {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<Fund>>>,
                    response: Response<ServerResponse<MutableList<Fund>>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        val fundsResponse = response.body()!!
                        if(!fundsResponse.error){
                            fundsResponse.data?.let { listOfFunds->
                                var totalFund = listOfFunds.sumOf { it.amount }

                                binding.txtPageTitle.text = "Total Fund Added $totalFund"

                                funds.clear()
                                funds.addAll(listOfFunds)

                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<MutableList<Fund>>>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun initViews() {

        if(!Constant.isManagerOrSuperUser()){
            binding.btnAddFund.visibility = View.GONE
        }else{
            binding.btnAddFund.setOnClickListener {
                showFundAddDialog()
            }
        }


        setDate(Constant.getCurrentDate())
        adapter = FundAdapter(this, funds){
            showFundEditDialog(it)
        }
        binding.recyFund.setHasFixedSize(true)
        binding.recyFund.layoutManager = LinearLayoutManager(this)
        binding.recyFund.adapter = adapter

        binding.txtMonthYear.setOnClickListener {
            showDateTimePicker()
        }


    }


    private fun showFundAddDialog() {
        var selectedDate : String = Constant.getCurrentDate()
        val editBinding = DialogEditPurchaseLayoutBinding.inflate(layoutInflater)


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(editBinding.root)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        editBinding.layUser.visibility  =View.GONE
        editBinding.btnDelete.visibility  =View.GONE


        editBinding.edtProducts.hint = "Comment"
        editBinding.edtAmount.hint = "Amount"
        editBinding.dateTxt.text = "Selected Date"
        editBinding.txtAmount.text = "Enter Fund Amount"
        editBinding.txtProduct.text = "Enter fund comment"
        editBinding.btnUpdate.text = "Add"

        editBinding.txtDate.text = selectedDate

        editBinding.txtDate.setOnClickListener {
            MyDatePicker(
                this,
                object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int, month: Int, year: Int) {

                    }

                    override fun dateString(date: String) {
                        selectedDate = date
                        editBinding.txtDate.text = date
                    }

                },
                Constant.getDay(Constant.getCurrentDate()).toInt(),
                Constant.getMonthNumber(Constant.getCurrentDate()).toInt(),
                Constant.getYear(Constant.getCurrentDate()).toInt(),
            ).create().show()
        }


        editBinding.btnUpdate.setOnClickListener {
            if(selectedDate.isNullOrEmpty()){
                shortToast("Date not valid")
                return@setOnClickListener
            }

            val comment = editBinding.edtProducts.text.toString()

            if(comment.isEmpty()){
                shortToast("Comment must not empty")
                return@setOnClickListener
            }


            var amount  = editBinding.edtAmount.text.toString().toFloatOrNull()


            if(amount==null){
                shortToast("Amount not valid")
                return@setOnClickListener
            }


            addFund(selectedDate!!, comment, amount, dialog)

        }

        editBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addFund(selectedDate: String, comment: String, amount: Float, dialog: Dialog) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .addFund(selectedDate, comment, amount)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    //
                    loadingDialog.hide()

                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            dialog.dismiss()
                            getFunds()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    //
                    loadingDialog.hide()
                }

            })
    }

    private fun showFundEditDialog(fund: Fund) {
        var selectedDate : String = fund.date
        val editBinding = DialogEditPurchaseLayoutBinding.inflate(layoutInflater)


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(editBinding.root)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        editBinding.layUser.visibility  =View.GONE


        editBinding.edtProducts.hint = "Comment"
        editBinding.edtAmount.hint = "Amount"
        editBinding.dateTxt.text = "Selected Date"
        editBinding.txtAmount.text = "Enter Fund Amount"
        editBinding.txtProduct.text = "Enter fund comment"

        editBinding.txtDate.text = selectedDate
        editBinding.edtAmount.setText(fund.amount.toString())
        editBinding.edtProducts.setText(fund.comment)

        editBinding.txtDate.setOnClickListener {
            MyDatePicker(
                this,
                object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int, month: Int, year: Int) {

                    }

                    override fun dateString(date: String) {
                        selectedDate = date
                        editBinding.txtDate.text = date
                    }

                },
                Constant.getDay(fund.date).toInt(),
                Constant.getMonthNumber(fund.date).toInt(),
                Constant.getYear(fund.date).toInt(),
            ).create().show()
        }


        editBinding.btnUpdate.setOnClickListener {
            if(selectedDate.isNullOrEmpty()){
                shortToast("Date not valid")
                return@setOnClickListener
            }

            val comment = editBinding.edtProducts.text.toString()

            if(comment.isEmpty()){
                shortToast("Comment must not empty")
                return@setOnClickListener
            }


            var amount  = editBinding.edtAmount.text.toString().toFloatOrNull()


            if(amount==null){
                shortToast("Amount not valid")
                return@setOnClickListener
            }


            editFund(fund.id, selectedDate, comment, amount, dialog)

        }

        editBinding.btnDelete.setOnClickListener {
            delete(fund.id, dialog)
        }

        editBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun delete(id: Int, dialog:Dialog) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .deleteFund(id)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    //
                    loadingDialog.hide()

                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            dialog.dismiss()
                            getFunds()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    //
                    loadingDialog.hide()
                }

            })
    }

    private fun editFund(id: Int, selectedDate: String, comment: String, amount: Float, dialog: Dialog) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .updateFund(id, selectedDate, comment, amount)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    //
                    loadingDialog.hide()

                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            dialog.dismiss()
                            getFunds()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    //
                    loadingDialog.hide()
                }

            })
    }

    @SuppressLint("SetTextI18n")
    fun showDateTimePicker() {
        MyDatePicker(this, object : MyDatePicker.OnDateSelectListener {
            override fun date(date: Int, month: Int, year: Int) {

            }

            override fun dateString(date: String) {
                setDate(date)
            }

        })
            .create()
            .show()

    }

    @SuppressLint("SetTextI18n")
    private fun setDate(date: String) {
        month = Constant.getMonthNumber(date)
        year = Constant.getYear(date)
        binding.txtMonthYear.text = Constant.getYear(date)+" "+ Constant.getMonthName(date)
        getFunds()
    }

    override fun onStart() {
        super.onStart()
        getFunds()
    }
}