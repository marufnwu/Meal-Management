package com.logicline.mydining.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.logicline.mydining.R
import com.logicline.mydining.adapter.PurchaseProductFieldAdapter
import com.logicline.mydining.databinding.ActivitySubmitPurchaseBinding
import com.logicline.mydining.models.PurchaseProduct
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubmitPurchaseActivity : BaseActivity(), MyDatePicker.OnDateSelectListener {
    lateinit var selectedDate : String
    private var isListItem = true
    private var isDepositToAcc = false
    private val purchaseProducts : MutableList<PurchaseProduct> = mutableListOf()
    lateinit var purchaseProductFieldAdapter: PurchaseProductFieldAdapter
    lateinit var binding: ActivitySubmitPurchaseBinding

    lateinit var loadingDialog: LoadingDialog
    var totalPurchase = 0f
    var isDeposit = 0

    var purchaseType = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)
        initAdapter()


        binding.txtDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.checkboxProductList.setOnCheckedChangeListener { _, b ->
            isListItem = b
            setLayoutType(b)
        }

        binding.checkboxDepositToAcc.setOnCheckedChangeListener { _, b ->
            isDepositToAcc = b
        }

        binding.imgAddPurchase.setOnClickListener {
            val name = binding.edtSingleName.text.toString()
            val price = binding.edtSinglePrice.text.toString()

            if(name.isNotEmpty() && price.isNotEmpty()){
                addPurchaseItem(name, price.toFloat())
            }else{
                shortToast("All filed are required")
            }
        }

        binding.checkboxDepositToAcc.setOnCheckedChangeListener { _, check ->
            isDeposit = if(check){
                1
            }else{
                0
            }
        }

        binding.btnSubmitPurchase.setOnClickListener {
            submit()
        }

        binding.radioGroupPurchaseType.setOnCheckedChangeListener { radioGroup, i ->

            purchaseType = if(i == R.id.radioBtnMealPurchase){
                1
            }else{
                2
            }

        }


        setDate(Constant.getCurrentDate())
    }

    private fun submit() {

        var type = "list"

        try {
            if(isListItem){

                if(purchaseProducts.size<1){
                    shortToast("Please add minimum one product")
                    return
                }

                if(totalPurchase<1){
                    shortToast("Total Amount must be greater than zero")
                    return
                }

                val productJson = Gson().toJson(purchaseProducts)

                loadingDialog.show()
                (application as MyApplication)
                    .myApi.requestListPurchase(productJson, selectedDate, totalPurchase, isDeposit, purchaseType)
                    .enqueue(object : Callback<GenericRespose> {
                        override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                            loadingDialog.hide()

                            if(response.isSuccessful && response.body()!=null){

                                shortToast(response.body()!!.msg)

                                if(!response.body()!!.error){
                                    purchaseProducts.clear()
                                    totalPurchase = 0F
                                    purchaseProductFieldAdapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                            loadingDialog.hide()
                        }

                    })



            }else{
                type = "single"

                val priceString = binding.edtPrice.text.toString()
                var price = 0f
                if(TextUtils.isDigitsOnly(priceString) && !priceString.isEmpty()){
                    price = priceString.toFloat()
                }else{
                    Toast.makeText(this, "price is not valid", Toast.LENGTH_SHORT).show()
                    return
                }

                if(price<1){
                    shortToast("Total Amount must be greater than zero")
                    return
                }
                val productDesc = binding.edtDesc.text.toString()

                if(productDesc.isEmpty()){
                    shortToast("Description is no valid")
                    return
                }

                loadingDialog.show()
                (application as MyApplication)
                    .myApi
                    .requestSinglePurchase(productDesc, selectedDate, price, isDeposit, purchaseType)
                    .enqueue(object : Callback<GenericRespose> {
                        override fun onResponse(
                            call: Call<GenericRespose>, response: Response<GenericRespose>) {

                            loadingDialog.hide()

                            if(response.isSuccessful && response.body()!=null){

                                shortToast(response.body()!!.msg)

                                if(!response.body()!!.error){
                                    binding.edtPrice.text.clear()
                                    binding.edtDesc.text.clear()
                                }
                            }

                        }

                        override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                            loadingDialog.hide()
                        }

                    })


            }
        }catch (e:Exception){
            loadingDialog.hide()
            shortToast(e.message)
        }





    }

    private fun addPurchaseItem(name: String, toFloat: Float) {
        purchaseProducts.add(PurchaseProduct(name, toFloat))
        totalPurchase+=toFloat
        binding.txtTotalPurchase.setText("Total Purchase amount ${totalPurchase}")
        binding.recyProducts.visibility = View.VISIBLE
        binding.txtEmpty.visibility  =View.GONE
        purchaseProductFieldAdapter.notifyDataSetChanged()
        binding.recyProducts.scrollToPosition(purchaseProducts.size-1)

        binding.edtSingleName.text.clear()
        binding.edtSinglePrice.text.clear()
        binding.edtSingleName.requestFocus()
    }

    private fun initAdapter() {
        binding.recyProducts.layoutManager = LinearLayoutManager(this)
        binding.recyProducts.setHasFixedSize(false)


        purchaseProductFieldAdapter = PurchaseProductFieldAdapter(this, purchaseProducts)
        purchaseProductFieldAdapter.fieldChange = object : PurchaseProductFieldAdapter.FieldChange {
            override fun onNameChange(name: String, position: Int) {
                if(position<=purchaseProducts.size){
                    Log.d("NamePosition", position.toString())
                    purchaseProducts.get(position).name  = name
                }
            }

            override fun onPriceChange(price: Float, position: Int) {
                if(position<=purchaseProducts.size){
                    Log.d("PricePosition", position.toString())

                    purchaseProducts.get(position).price  = price
                }
            }

        }
        binding.recyProducts.adapter  = purchaseProductFieldAdapter

    }

    private fun addField(){

        val lastField  = purchaseProducts.get(purchaseProducts.size-1)

        if(lastField.name!!.isNotEmpty() && lastField.price!! >0){
            purchaseProducts.add(PurchaseProduct("", 0f))
            purchaseProductFieldAdapter.notifyItemChanged(purchaseProducts.size-1)

        }else{
            shortToast("Please insert last field first")
        }
        purchaseProductFieldAdapter.notifyDataSetChanged()
        Log.d("adad", Gson().toJson(purchaseProducts))



    }

    private fun setLayoutType(b: Boolean) {

        if(b){
            binding.layoutDescription.visibility  =View.GONE
            binding.layoutList.visibility  =View.VISIBLE
        }else{
            binding.layoutDescription.visibility  =View.VISIBLE
            binding.layoutList.visibility  =View.GONE
        }

    }

    @SuppressLint("SetTextI18n")
    fun showDateTimePicker() {
        MyDatePicker(this, this)
            .create()
            .show()

    }

    override fun date(date: Int?, month: Int?, year: Int?) {

    }

    override fun dateString(date: String) {
        setDate(date)
    }

    @SuppressLint("SetTextI18n")
    private fun setDate(date: String) {
        selectedDate = date
        binding.txtDate.text = date+" "+ Constant.getDayNameFromDate(date)
    }
}