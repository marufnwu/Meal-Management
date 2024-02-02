package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.DepositHistoryAdapter
import com.logicline.mydining.databinding.ActivityDepositHistoryBinding
import com.logicline.mydining.databinding.DialogEditDepositLayoutBinding
import com.logicline.mydining.models.DepositHistory
import com.logicline.mydining.models.response.DepositHistoryResponse
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepositHistoryActivity : BaseActivity() {
    lateinit var myFullScreenAd: MyFullScreenAd
    enum class Type{
        MESS,
        SINGLE_USER
    }

    lateinit var binding: ActivityDepositHistoryBinding
    lateinit var adapter: DepositHistoryAdapter

    private var depositHistory : MutableList<DepositHistory> = mutableListOf()
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

        adapter = DepositHistoryAdapter(this, depositHistory)

        adapter.onItemAction = object : DepositHistoryAdapter.OnItemAction {
            override fun onEdit(depositHistory: DepositHistory, pos: Int) {
                showEditDailog(depositHistory, pos)
            }
        }


        binding.recyDepositHistory.adapter = adapter
    }

    private fun showEditDailog(depositHistory: DepositHistory, pos: Int) {

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




        editBinding.txtName.text = depositHistory.name
        editBinding.txtDate.text = depositHistory.date
        editBinding.edtAmount.setText(depositHistory.amount.toString())


        editBinding.txtDate.setOnClickListener {
            MyDatePicker(
                this,
                object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int?, month: Int?, year: Int?) {

                    }

                    override fun dateString(date: String) {
                        depositHistory.date = date
                        editBinding.txtDate.text = date
                    }

                },
                Constant.getDay(depositHistory.date).toInt(),
                Constant.getMonthNumber(depositHistory.date).toInt(),
                Constant.getYear(depositHistory.date).toInt(),
            ).create().show()
        }


        editBinding.btnUpdate.setOnClickListener {
            if(depositHistory.date.isEmpty()){
                shortToast("Date not valid")
                return@setOnClickListener
            }

            var amount :Float? = editBinding.edtAmount.text.toString().toFloatOrNull()


            if(amount==null){
                shortToast("Amount not valid")
                return@setOnClickListener
            }

            depositHistory.amount = amount

            update(depositHistory.id, depositHistory.amount, depositHistory.date, pos, dialog)



        }

        editBinding.btnDelete.setOnClickListener {
            delete(depositHistory.id, pos, dialog)
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
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
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

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
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
        val month = intent?.getStringExtra(Constant.MONTH)
        val year = intent?.getStringExtra(Constant.YEAR)
        val messId = intent?.getStringExtra(Constant.MESS_ID)

        type?.let {
            if(it==Type.SINGLE_USER.name){
                //single user history
                val userId = intent?.getStringExtra(Constant.USER_ID)
                userId?.let {
                    getSingleUserHistory(it, year, month, messId)
                }

            }else{
                //full mess history
            }
        }
    }

    private fun getSingleUserHistory(id: String, year: String?, month: String?, messId: String?) {



        if(year==null || month == null|| messId == null){
            shortToast("Something went wrong!!")
            return
        }

        loadingDialog.show()

        (application as MyApplication)
            .myApi
            .getDepositByUserIdDate(id, year, month, messId)
            .enqueue(object : Callback<ServerResponse<DepositHistoryResponse>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ServerResponse<DepositHistoryResponse>>, response: Response<ServerResponse<DepositHistoryResponse>>) {
                    if (response.isSuccessful && response.body()!=null){

                        loadingDialog.hide()

                        val res = response.body()!!

                        if(!res.error){

                            res.data?.let {
                                if(it.history.isNotEmpty()){
                                    binding.layoutParent.visibility = View.VISIBLE
                                    depositHistory.clear()
                                    depositHistory.addAll(res.data!!.history)
                                    adapter.notifyDataSetChanged()
                                }

                                binding.txtTotal.text  =it.total.toString()

                            }



                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<DepositHistoryResponse>>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)

                }

            })
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