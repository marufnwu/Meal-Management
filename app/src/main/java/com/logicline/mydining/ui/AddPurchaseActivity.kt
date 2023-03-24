package com.logicline.mydining.ui

import android.R
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import com.logicline.mydining.databinding.ActivityAddPurchaseBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.UserListResponse
import com.logicline.mydining.utils.*
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPurchaseActivity : BaseActivity(), MyDatePicker.OnDateSelectListener, AdapterView.OnItemSelectedListener {
    private lateinit var myFullScreenAd: MyFullScreenAd
    var selectedUser : User? = null
    lateinit var binding : ActivityAddPurchaseBinding
    lateinit var loadingDialog : LoadingDialog
    private var userList: List<User>? = null
    private var type = 0
    private var isDeposit = 0

    lateinit var selectedDate : String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        myFullScreenAd = MyFullScreenAd(this, true)

        type = intent.getIntExtra(Constant.PURCHASE_TYPE, 0)

        if(type==1){
            supportActionBar?.title = "Meal Purchase"
            binding.btnAddPurchase.text = "Add Meal Purchase"

            binding.rGroupPurchaseType.check(com.logicline.mydining.R.id.rButtonMealPurchase)

        }else if(type==2){
            supportActionBar?.title ="Others Cost"
            binding.btnAddPurchase.text = "Add Others Cost"
            binding.rGroupPurchaseType.check(com.logicline.mydining.R.id.rButtonOtherPurchase)
        }else{
            supportActionBar?.title = "Purchase"
            binding.btnAddPurchase.text = "Add Purchase"
        }

        loadingDialog = LoadingDialog(this)
        binding.spinnerMember.onItemSelectedListener = this
        binding.checkboxDeposit.setOnCheckedChangeListener { _, check ->
            isDeposit = if(check){
                1
            }else{
                0
            }
        }

        binding.rGroupPurchaseType.setOnCheckedChangeListener { radioGroup, i ->
            if(i==com.logicline.mydining.R.id.rButtonMealPurchase){
                type =1
            }else{
                type = 2
            }
        }

        setDate(Constant.getCurrentDate())
        binding.txtDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.btnAddPurchase.setOnClickListener {
            addPurchase()
        }





    }

    override fun onResume() {
        super.onResume()
        getUsersList()
    }

    private fun getUsersList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi.getInitiatedUsers(1, selectedDate)
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

        if(type<1 || type>2){
            shortToast("Please select purchase type")
            return
        }

        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .addPurchase(selectedUser?.id!!, selectedDate, productDesc, price, type, isDeposit)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null) {
                        shortToast(response.body()?.msg)

                        if(!response.body()!!.error){
                            binding.edtDesc.text.clear()
                            binding.edtPrice.text.clear()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    shortToast("Something went wrong")
                    loadingDialog.hide()
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
        getUsersList()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(p2>0){
            userList?.let { userList->
                selectedUser = userList[p2-1]
                showDepositCheckbox()
            }
        }else{
            selectedUser = null
            hideDepositCheckbox()
        }
    }

    private fun showDepositCheckbox() {
        selectedUser?.let {
            binding.checkboxDeposit.visibility = View.VISIBLE
            binding.checkboxDeposit.isChecked = false
            it.name?.let {
                binding.checkboxDeposit.text= "Also deposit to $it account"
            }
        }
    }

    private fun hideDepositCheckbox() {
        binding.checkboxDeposit.visibility = View.GONE
        binding.checkboxDeposit.isChecked = false
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

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