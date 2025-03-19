package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityAddDepositBinding
import com.logicline.mydining.models.Deposit
import com.logicline.mydining.models.MessUser
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.*
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDepositActivity : BaseActivity(true) , MyDatePicker.OnDateSelectListener, AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityAddDepositBinding
    lateinit var loadingDialog : LoadingDialog
    private var userList: List<MessUser>? = null

    var selectedUser : MessUser? = null
    lateinit var selectedDate : String
    lateinit var myFullScreenAd: MyFullScreenAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_deposit)
        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog = LoadingDialog(this)
        binding.spinnerMember.onItemSelectedListener = this


        binding.txtDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddPurchase.setOnClickListener {
            addDeposit()
        }

        setDate(Constant.getCurrentDate())


    }

    @SuppressLint("SetTextI18n")
    fun showDateTimePicker() {
        MyDatePicker(this, this)
            .create()
            .show()

    }

    private fun addDeposit() {
        val priceString = binding.edtAmount.text.toString()
        var amount = 0f
        if(TextUtils.isDigitsOnly(priceString) && priceString.isNotEmpty()){
            amount = priceString.toFloat()
        }else{
            Toast.makeText(this, "Amount is not valid", Toast.LENGTH_SHORT).show()
            return
        }

        if(selectedUser==null){
            shortToast("Select Member")
            return
        }
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .addDeposit(selectedUser?.id!!, selectedDate, amount)
            .enqueue(object : Callback<ServerResponse<Deposit>> {
                override fun onResponse(
                    call: Call<ServerResponse<Deposit>>,
                    response: Response<ServerResponse<Deposit>>
                ) {
                    loadingDialog.hide()

                    if (response.isSuccessful && response.body()!=null){
                        shortToast(response.body()?.msg)
                        if(!response.body()!!.error){
                            binding.edtAmount.text.clear()
                        }

                    }
                }

                override fun onFailure(call: Call<ServerResponse<Deposit>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })



    }


    private fun getUsersList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi.getInitiatedUsers()
            .enqueue(object: Callback<ServerResponse<List<MessUser>>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<ServerResponse<List<MessUser>>>, response: Response<ServerResponse<List<MessUser>>>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            userList  = userListResponse.data!!
                            setUsersToSpinner(userList!!)

                        }else{
                            shortToast(response.body()?.msg)
                        }
                    }
                }
                override fun onFailure(call: Call<ServerResponse<List<MessUser>>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setUsersToSpinner(userList: List<MessUser>) {
        val usersArray = arrayListOf<String?>()
        usersArray.add("Select Uer")
        userList.listIterator().forEach { member->
            usersArray.add(member.user?.name)
        }

        Log.d("Member", usersArray.size.toString())


        ArrayAdapter(this,  android.R.layout.simple_spinner_item, usersArray)
            .also { adapter->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerMember.adapter = adapter

            }

    }
    override fun date(date: Int, month: Int, year: Int) {

    }

    override fun dateString(date: String) {
        setDate(date)
    }


    @SuppressLint("SetTextI18n")
    private fun setDate(date: String) {
        selectedDate = date
        binding.txtDate.text = date+" "+ Constant.getDayNameFromDate(date)
        getUsersList()
    }



    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(p2>0){
            userList?.let { userList->
                selectedUser = userList[p2-1]
            }
        }else{
            selectedUser = null
        }
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onResume() {
        super.onResume()
        getUsersList()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }
}