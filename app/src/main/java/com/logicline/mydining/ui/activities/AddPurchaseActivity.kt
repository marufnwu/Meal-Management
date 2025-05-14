package com.logicline.mydining.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.logicline.mydining.MyApplication
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityAddPurchaseBinding
import com.logicline.mydining.data.enums.PurchaseType
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.Purchase
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.*
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPurchaseActivity : BaseActivity(), MyDatePicker.OnDateSelectListener, AdapterView.OnItemSelectedListener {
    private var purchaseType: PurchaseType? = null
    private lateinit var myFullScreenAd: MyFullScreenAd
    var selectedUser : MessUser? = null
    lateinit var binding : ActivityAddPurchaseBinding
    lateinit var loadingDialog : LoadingDialog
    private var userList: List<MessUser>? = null
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


        val type = intent.getStringExtra(Constant.PURCHASE_TYPE) ?: PurchaseType.PURCHASE.value

         purchaseType = PurchaseType.fromValue(type)

        when (purchaseType) {
            PurchaseType.PURCHASE -> {
                supportActionBar?.title = getString(R.string.add_purchase)
                binding.btnAddPurchase.text = getString(R.string.add_purchase)
                binding.rGroupPurchaseType.check(R.id.rButtonMealPurchase)
            }
            PurchaseType.OTHER_PURCHASE -> {
                supportActionBar?.title = getString(R.string.other_purchase)
                binding.btnAddPurchase.text = getString(R.string.add_other_purchase)
                binding.rGroupPurchaseType.check(R.id.rButtonOtherPurchase)
            }
            else -> {
                supportActionBar?.title = getString(R.string.purchases)
                binding.btnAddPurchase.text = getString(R.string.add_purchase)
            }
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
            if(i== R.id.rButtonMealPurchase){
                purchaseType = PurchaseType.PURCHASE
            }else{
                purchaseType = PurchaseType.OTHER_PURCHASE
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

        if(purchaseType == null){
            shortToast("Please select purchase type")
            return
        }

        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .addPurchase(selectedUser?.id!!, selectedDate, productDesc, price, purchaseType!!.value, isDeposit)
            .enqueue(object : Callback<ServerResponse<Purchase>> {
                override fun onResponse(
                    call: Call<ServerResponse<Purchase>>, response: Response<ServerResponse<Purchase>>) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null) {
                        shortToast(response.body()?.msg)

                        if(!response.body()!!.error){
                            binding.edtDesc.text.clear()
                            binding.edtPrice.text.clear()
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Purchase>>, t: Throwable) {
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
            it.user?.name?.let {
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