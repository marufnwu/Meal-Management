package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.PurchaseListAdapter
import com.logicline.mydining.databinding.ActivityPurchasesBinding
import com.logicline.mydining.databinding.DialogEditPurchaseLayoutBinding
import com.logicline.mydining.enums.MessPermission
import com.logicline.mydining.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.enums.PurchaseType
import com.logicline.mydining.models.Purchase
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.PurchaseListResponse
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchasesActivity : BaseActivity() {
    lateinit var myFullScreenAd: MyFullScreenAd
    private var purchaseList: MutableList<Purchase> = mutableListOf()
    lateinit var adpter: PurchaseListAdapter
    lateinit var binding: ActivityPurchasesBinding
    var month: String = Constant.getCurrentMonthNumber()
    var year: String = Constant.getCurrentYear()
    lateinit var loadingDialog: LoadingDialog
    var type: PurchaseType? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog = LoadingDialog(this)

        intent?.let {
            type = it.getStringExtra(Constant.PURCHASE_TYPE)?.let { PurchaseType.valueOf(it) }
            it.getStringExtra(Constant.YEAR)?.let {
                year = it
            }

            it.getStringExtra(Constant.MONTH)?.let {
                month = it

            }
        }



        if (type == PurchaseType.PURCHASE) {
            supportActionBar?.title = getString(R.string.purchases)

            binding.btnAddPurchase.text = getString(R.string.add_purchase)
        } else if (type == PurchaseType.OTHER_PURCHASE) {
            supportActionBar?.title = getString(R.string.other_purchase)
            binding.btnAddPurchase.text = getString(R.string.add_other_purchase)
        }

        if (LocalDB.getUserData()?.messUser.hasAnyPermission(MessPermission.PURCHASE_MANAGEMENT, MessPermission.PURCHASE_ADD)) {
            binding.layoutAdminPurchase.visibility = View.VISIBLE

        } else {
            binding.layoutAdminPurchase.visibility = View.GONE
        }

        binding.recyPurchase.setHasFixedSize(true)
        binding.recyPurchase.layoutManager = LinearLayoutManager(this)


        //admin add purchase
        binding.btnAddPurchase.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddPurchaseActivity::class.java
                ).putExtra(Constant.PURCHASE_TYPE, type)
            )
        }

        adpter = PurchaseListAdapter(this@PurchasesActivity, purchaseList)

        adpter.onAction = object : PurchaseListAdapter.OnAction {
            override fun onItemClick(purchase: Purchase, position: Int) {
                showEditDialog(purchase, position)
            }

        }

        binding.recyPurchase.adapter = adpter

        binding.monthPicker.builder(
            null,
            mYear = year.toInt(),
            mMonth = month.toInt(),
            mDay = 1
        ).onDateSelectListener = object : MyDatePicker.OnDateSelectListener {
            override fun date(date: Int, month: Int, year: Int) {

                setDate(year.toString(), month.toString())
            }

            override fun dateString(date: String) {
            }

        }

    }

    private fun setDate(year: String, month: String) {
        this.year = year
        this.month = month
        getPurchaseList()
    }

    private fun showEditDialog(purchase: Purchase, position: Int) {
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




        editBinding.txtName.text = purchase.messUSer.user?.name
        editBinding.txtDate.text = purchase.date
        editBinding.edtProducts.setText(purchase.product)
        editBinding.edtAmount.setText(purchase.price.toString())


        editBinding.txtDate.setOnClickListener {
            MyDatePicker(
                this,
                object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int, month: Int, year: Int) {

                    }

                    override fun dateString(date: String) {
                        purchase.date = date
                        editBinding.txtDate.text = date
                    }

                },
                Constant.getDay(purchase.date).toInt(),
                Constant.getMonthNumber(purchase.date).toInt(),
                Constant.getYear(purchase.date).toInt(),
            ).create().show()
        }


        editBinding.btnUpdate.setOnClickListener {
            if (purchase.date.isEmpty()) {
                shortToast("Date not valid")
                return@setOnClickListener
            }

            val products = editBinding.edtProducts.text.toString()

            if (products.isEmpty()) {
                shortToast("Products must not empty")
                return@setOnClickListener
            }

            if (type == null) {
                shortToast("Purchase type not valid")
                return@setOnClickListener
            }

            purchase.product = products


            var price = editBinding.edtAmount.text.toString().toFloatOrNull()


            if (price == null) {
                shortToast("Amount not valid")
                return@setOnClickListener
            }

            purchase.price = price

            update(purchase, position, dialog)

        }

        editBinding.btnDelete.setOnClickListener {
            delete(purchase.id.toInt(), position, dialog)
        }



        editBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }


    private fun update(purchase: Purchase, position: Int, dialog: Dialog) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .updatePurchase(
                purchase.id,
                purchase.price,
                purchase.date,
                purchase.product,
                type!!.value
            )
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body() != null) {

                        shortToast(response.body()!!.msg)

                        if (!response.body()!!.error) {
                            //success

                            dialog.dismiss()
                            getPurchaseList()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast("Something went wrong, please try again")
                }

            })
    }

    private fun delete(id: Int, position: Int, dialog: Dialog) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .deletePurchase(id, type!!.value)
            .enqueue(object : Callback<ServerResponse<Void>> {
                override fun onResponse(
                    call: Call<ServerResponse<Void>>,
                    response: Response<ServerResponse<Void>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body() != null) {

                        shortToast(response.body()!!.msg)

                        if (!response.body()!!.error) {
                            //success

                            dialog.dismiss()
                            getPurchaseList()
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast("Something went wrong, please try again")
                }

            })
    }

    override fun onStart() {
        super.onStart()
        getPurchaseList()

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("type", type?.name)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        type = savedInstanceState!!.getString("type")?.let { PurchaseType.valueOf(it) }

    }

    fun getPurchaseList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getPurchases(type = type!!.value)
            .enqueue(object : Callback<ServerResponse<PurchaseListResponse>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ServerResponse<PurchaseListResponse>>,
                    response: Response<ServerResponse<PurchaseListResponse>>
                ) {
                    loadingDialog.hide()

                    if (response.isSuccessful && response.body() != null) {
                        val purchaseListResponse = response.body()!!
                        if (!purchaseListResponse.error) {

                            var purchaseType = ""
                            if (type == PurchaseType.PURCHASE) {
                                purchaseType = "Total Purchase"
                            } else {
                                purchaseType = "Total Others Cost"
                            }
                            binding.txtTotalPurchase.text =
                                purchaseType + " " + purchaseListResponse.data?.totalPurchase

                            purchaseListResponse.data?.purchases.let {
                                purchaseList.clear()
                                purchaseList.addAll(it!!)
                                adpter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<PurchaseListResponse>>,
                    t: Throwable
                ) {
                    loadingDialog.hide()
                }

            })
    }

    override fun onBackPressed() {
        myFullScreenAd.showAd()
        super.onBackPressed()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }
}