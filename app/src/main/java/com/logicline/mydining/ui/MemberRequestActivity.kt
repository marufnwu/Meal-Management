package com.logicline.mydining.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.MessRequestAdapter
import com.logicline.mydining.databinding.ActivityMemberRequestBinding
import com.logicline.mydining.databinding.ActivitySwitchMessBinding
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

class MemberRequestActivity : BaseActivity() {
    lateinit var binding: ActivityMemberRequestBinding
    lateinit var loadingDialog: LoadingDialog
    private val requests : MutableList<MessRequest>  = mutableListOf()
    lateinit var adapter: MessRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.memeber_request)
        binding = ActivityMemberRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        initView()
        getRequestHistory()
    }

    fun getRequestHistory(){
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .messJoinRequest()
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

    private fun initView() {

        adapter = MessRequestAdapter(this, requests)
        binding.recyHistory.layoutManager = LinearLayoutManager(this)
        binding.recyHistory.setHasFixedSize(true)
        binding.recyHistory.adapter = adapter

        adapter.onCancel = {
            cacelRequest(it)
        }

        adapter.onAccept = {
            acceptRequest(it)
        }

    }

    private fun acceptRequest(it: Int) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .acceptMessMemberJoinRequest(it)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            getRequestHistory()
                        }

                        shortToast(response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
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
                            getRequestHistory()
                        }

                        shortToast(response.body()!!.msg)
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }
}