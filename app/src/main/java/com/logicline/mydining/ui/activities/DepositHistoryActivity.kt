package com.logicline.mydining.ui.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.DepositHistoryAdapter
import com.logicline.mydining.databinding.ActivityDepositHistoryBinding
import com.logicline.mydining.databinding.DialogEditDepositLayoutBinding
import com.logicline.mydining.data.models.Deposit
import com.logicline.mydining.data.models.DepositHistory
import com.logicline.mydining.data.models.response.GenericRespose
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class DepositHistoryActivity : BaseActivity() {
    lateinit var myFullScreenAd: MyFullScreenAd
    enum class Type{
        MESS,
        SINGLE_USER
    }

    lateinit var binding: ActivityDepositHistoryBinding
    lateinit var adapter: DepositHistoryAdapter

    private var deposits : MutableList<Deposit> = mutableListOf()
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepositHistoryBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.deposit_history)

        setContentView(binding.root)
        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog = LoadingDialog(this)

        setupRecyclerView()

        initCall()
    }

    private fun setupRecyclerView() {
        binding.recyDepositHistory.layoutManager = LinearLayoutManager(this)
        binding.recyDepositHistory.setHasFixedSize(true)

        adapter = DepositHistoryAdapter(this, deposits)

        adapter.onItemAction = object : DepositHistoryAdapter.OnItemAction {
            override fun onEdit(deposit: Deposit, pos: Int) {
                showEditDailog(deposit, pos)
            }
        }


        binding.recyDepositHistory.adapter = adapter
    }

    private fun showEditDailog(deposit: Deposit, pos: Int) {

        val editBinding = DialogEditDepositLayoutBinding.inflate(layoutInflater)


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




        editBinding.txtName.text = deposit.messUser?.user?.name
        editBinding.txtDate.text = deposit.date
        editBinding.edtAmount.setText(deposit.amount.toString())


        editBinding.txtDate.setOnClickListener {
            MyDatePicker(
                this,
                object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int, month: Int, year: Int) {

                    }

                    override fun dateString(date: String) {
                        deposit.date = date
                        editBinding.txtDate.text = date
                    }

                },
                Constant.getDay(deposit.date).toInt(),
                Constant.getMonthNumber(deposit.date).toInt(),
                Constant.getYear(deposit.date).toInt(),
            ).create().show()
        }


        editBinding.btnUpdate.setOnClickListener {
            if(deposit.date.isEmpty()){
                shortToast("Date not valid")
                return@setOnClickListener
            }

            var amount :Float? = editBinding.edtAmount.text.toString().toFloatOrNull()


            if(amount==null){
                shortToast("Amount not valid")
                return@setOnClickListener
            }

            deposit.amount = amount

            update(deposit.id, deposit.amount, deposit.date, pos, dialog)



        }

        editBinding.btnDelete.setOnClickListener {
            delete(deposit.id, pos, dialog)
        }



        editBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }


    private fun update(id: Int, amount:Float, date:String, pos: Int, dialog: Dialog){
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .updateDeposit(id, amount, date)
            .enqueue(object : Callback<ServerResponse<Void>> {
                override fun onResponse(
                    call: Call<ServerResponse<Void>>,
                    response: Response<ServerResponse<Void>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){

                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            //success

                            dialog.dismiss()
                            initCall()
                        }
                    }

                }

                override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun delete(id: Int, pos: Int, dialog: Dialog) {

        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .deleteDeposit(id)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>, response: Response<GenericRespose>) {

                    loadingDialog.hide()

                    if(response.isSuccessful && response.body()!=null){

                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            //success

                            dialog.dismiss()
                            initCall()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })

    }

    private fun initCall(){
        val type = intent?.getStringExtra(Constant.HISTORY_TYPE)

        type?.let {
            if(it==Type.SINGLE_USER.name){
                //single user history
                val messUserId = intent?.getIntExtra(Constant.MESS_USER_ID, 0)
                messUserId?.let {
                    getSingleUserHistory(messUserId)
                }

            }else{
                //full mess history
            }
        }
    }

    private fun getSingleUserHistory(id: Int) {

        loadingDialog.show()

        (application as MyApplication)
            .myApi
            .getDepositByUserIdDate(id)
            .enqueue(object : Callback<ServerResponse<DepositHistory>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ServerResponse<DepositHistory>>, response: Response<ServerResponse<DepositHistory>>) {
                    if (response.isSuccessful && response.body()!=null){

                        loadingDialog.hide()

                        val res = response.body()!!

                        if(!res.error){

                            res.data?.let {
                                if(it.deposits.isNotEmpty()){
                                    binding.layoutParent.visibility = View.VISIBLE
                                    deposits.clear()
                                    deposits.addAll(res.data!!.deposits)
                                    adapter.notifyDataSetChanged()
                                }

                                binding.txtTotal.text  = String.format(Locale.getDefault(), it.totalAmount.toString())

                            }



                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<DepositHistory>>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)

                }

            })
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