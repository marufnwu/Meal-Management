package com.maruf.messmanagement.ui

import android.R
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.maruf.messmanagement.databinding.ActivityAddPurchaseBinding
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

class AddPurchaseActivity : AppCompatActivity(), MyDatePicker.OnDateSelectListener, AdapterView.OnItemSelectedListener {
    var selectedUser : User? = null
    lateinit var binding : ActivityAddPurchaseBinding
    lateinit var loadingDialog : LoadingDialog
    private var userList: List<User>? = null
    private var type = 0

    lateinit var selectedDate : String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra(Constant.PURCHASE_TYPE, 0)

        if(type==1){
            binding.txtPageTitle.text = "Purchase"
            binding.btnAddPurchase.text = "Add Purchase"
        }else{
            binding.txtPageTitle.text ="Others Cost"
            binding.btnAddPurchase.text = "Add Others Cost"

        }

        loadingDialog = LoadingDialog(this)
        binding.spinnerMember.onItemSelectedListener = this

        setDate(Constant.getCurrentDate())
        binding.txtDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddPurchase.setOnClickListener {
            addPurchase()
        }

        setDate(Constant.getCurrentDate())


        getUsersList()

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


        ArrayAdapter(this,  R.layout.simple_spinner_item, usersArray)
            .also { adapter->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerMember.adapter = adapter


            }

    }



    private fun addPurchase() {
        val priceString = binding.edtPrice.text.toString()
        var price = 0
        if(TextUtils.isDigitsOnly(priceString) && !priceString.isEmpty()){
            price = priceString.toInt()
        }else{
            Toast.makeText(this, "price is not valid", Toast.LENGTH_SHORT).show()
            return
        }

        val productDesc = binding.edtDesc.text.toString()

        if(productDesc.isEmpty()){
            shortToast("Description is no valid")
            return
        }

        if(selectedUser==null){
            shortToast("Select Member")
            return
        }

        (application as MyApplication)
            .myApi
            .addPurchase(selectedUser?.id!!, selectedDate, productDesc, price, type)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    if (response.isSuccessful && response.body()!=null) shortToast(response.body()?.msg)
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                }

            })



    }

    @SuppressLint("SetTextI18n")
    fun showDateTimePicker() {
        MyDatePicker(this, this)
            .create()
            .show()

    }

    override fun date(date: Int, month: Int, year: Int) {

    }

    override fun dateString(date: String) {
        setDate(date)
    }

    @SuppressLint("SetTextI18n")
    private fun setDate(date: String) {
        selectedDate = date
        binding.txtDate.text = date+" "+Constant.getDayNameFromDate(date)
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