package com.logicline.mydining.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityMessInfoBinding
import com.logicline.mydining.enums.MessStatus
import com.logicline.mydining.models.Mess
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessInfoActivity : BaseActivity() {
    lateinit var binding: ActivityMessInfoBinding
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.mess_info)
        binding = ActivityMessInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        //getMessInfo()
        setData(LocalDB.getUserData()?.messUser?.mess)
    }

    private fun getMessInfo() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getMessInfo()
            .enqueue(object: Callback<ServerResponse<Mess>> {
                override fun onResponse(
                    call: Call<ServerResponse<Mess>>,
                    response: Response<ServerResponse<Mess>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            setData(response.body()!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Mess>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setData(data: Mess?) {
        data?.let {
            binding.txtMessName.text = data.name
            binding.txtMessId.text = data.id.toString()
            binding.txtMessCreated.text = data.createdAt.toString()
            binding.txtStatus.text = MessStatus.fromValue(data.status)?.value?: "Undefined"
        }
    }
}