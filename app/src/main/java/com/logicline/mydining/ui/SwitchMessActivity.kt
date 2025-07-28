package com.logicline.mydining.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.MessRequestAdapter
import com.logicline.mydining.databinding.ActivityPreviousMonthBinding
import com.logicline.mydining.databinding.ActivitySwitchMessBinding
import com.logicline.mydining.models.Mess
import com.logicline.mydining.models.MessRequest
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SwitchMessActivity : BaseActivity() {
    lateinit var binding: ActivitySwitchMessBinding
    lateinit var loadingDialog: LoadingDialog
    private val requests : MutableList<MessRequest>  = mutableListOf()
    lateinit var adapter:MessRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.switch_mess)
        binding = ActivitySwitchMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        initView()


        getJoinHistory()


    }

    private fun initView() {

        adapter = MessRequestAdapter(this, requests)
        binding.recyHistory.layoutManager = LinearLayoutManager(this)
        binding.recyHistory.setHasFixedSize(true)
        binding.recyHistory.adapter = adapter

        adapter.onCancel = {
            cacelRequest(it)
        }


        binding.btnSwitchMess.setOnClickListener {
            showDialog()
        }
    }

    private fun cacelRequest(it: Int) {
        loadingDialog.hide()
        (application as MyApplication)
            .myApi
            .cancelMessMemberJoinRequest(it)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            getJoinHistory()
                        }

                        shortToast(response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_switch_mess_layout)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<Button>(R.id.btnSwitch)
            .setOnClickListener {
                val messId = dialog.findViewById<EditText>(R.id.edtMessId).text.toString()

                if(messId.isNullOrBlank()){
                    shortToast("Please enter mess id")
                    return@setOnClickListener
                }
                dialog.hide()
                sendRequest(messId)

            }

        dialog.findViewById<Button>(R.id.btnCancel)
            .setOnClickListener {
                dialog.dismiss()
            }

        dialog.show()
    }

    private fun sendRequest(messId: String) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .messSwitchRequest(messId)
            .enqueue(object: Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            getJoinHistory()
                        }

                        shortToast(response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    fun getJoinHistory(){
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .userJoinHistory()
            .enqueue(object: Callback<ServerResponse<MutableList<MessRequest>>> {
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<MessRequest>>>,
                    response: Response<ServerResponse<MutableList<MessRequest>>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            requests.clear()
                            requests.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                        }else{
                            shortToast(response.body()!!.msg)
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<MutableList<MessRequest>>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })

    }
}