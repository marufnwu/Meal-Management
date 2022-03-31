package com.maruf.messmanagement.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.maruf.messmanagement.R
import com.maruf.messmanagement.databinding.ActivityAddDepositBinding
import com.maruf.messmanagement.databinding.LayoutDepositItemBinding
import com.maruf.messmanagement.models.User
import com.maruf.messmanagement.models.response.GenericRespose
import com.maruf.messmanagement.models.response.UserListResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import com.maruf.messmanagement.utils.MyDatePicker
import com.maruf.messmanagement.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddDepositActivity : AppCompatActivity() , MyDatePicker.OnDateSelectListener, AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityAddDepositBinding
    lateinit var loadingDialog : LoadingDialog
    private var userList: List<User>? = null

    var selectedUser : User? = null
    lateinit var selectedDate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDepositBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)
        binding.spinnerMember.onItemSelectedListener = this


        binding.txtDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddPurchase.setOnClickListener {
            addDeposit()
        }

        setDate(Constant.getCurrentDate())

        getUsersList()
    }

    @SuppressLint("SetTextI18n")
    fun showDateTimePicker() {
        MyDatePicker(this, this)
            .create()
            .show()

    }

    private fun addDeposit() {
        val priceString = binding.edtAmount.text.toString()
        var amount = 0
        if(TextUtils.isDigitsOnly(priceString) && !priceString.isEmpty()){
            amount = priceString.toInt()
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
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()

                    if (response.isSuccessful && response.body()!=null) shortToast(response.body()?.msg)
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })



    }


    private fun getUsersList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi.getUsers(1)
            .enqueue(object: Callback<UserListResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            userList  = userListResponse.userList!!
                            setUsersToSpinner(userList!!)

                        }else{
                            shortToast(response.body()?.msg)
                        }
                    }
                }
                override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setUsersToSpinner(userList: List<User>) {
        val usersArray = arrayListOf<String?>()
        usersArray.add("Select Uer")
        userList.listIterator().forEach { member->
            usersArray.add(member.name)
            Log.d("Member", member.name!!)
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
}