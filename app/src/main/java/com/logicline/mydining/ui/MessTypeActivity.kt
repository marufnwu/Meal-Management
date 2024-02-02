package com.logicline.mydining.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityMessTypeBinding
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.InitialDataResponse
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessTypeActivity : AppCompatActivity() {
    private var currentType: Int? = null
    private var selectedItem: Int? = null
    lateinit var binding : ActivityMessTypeBinding
    lateinit var loadingDialog : LoadingDialog

    private var spinnerItems = arrayOf("Select mess type", "Auto", "Manual")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        currentType = LocalDB.getInitialData()?.messData?.type!!


        initView()
    }

    private fun initView() {
        binding.btnSubmit.setOnClickListener {
            if(selectedItem!=null){
                changeMessType(selectedItem!!)
            }else{
                Toast.makeText(this, "Please select your preferred mess type", Toast.LENGTH_SHORT).show()
            }

        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
        binding.spinnerType.setSelection(currentType!!)


        binding.spinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2==0){
                    selectedItem  =null
                    return
                }
                selectedItem = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectedItem = null
            }

        }
    }

    private fun changeMessType(selectedItem: Int) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .setMessType(selectedItem)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    response.body()?.let {
                        Toast.makeText(this@MessTypeActivity, it.msg, Toast.LENGTH_SHORT).show()
                        if (it.error){
                            return
                        }
//                        val initialData = LocalDB.getInitialData()
//
//                        val messData = initialData?.messData
//                        messData?.type = selectedItem
//                        initialData?.messData = messData
//
//                        LocalDB.saveInitialData(initialData!!)

                        getInitialData()
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    Toast.makeText(this@MessTypeActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getInitialData() {
        (application as MyApplication)
            .myApi
            .getInitialData(BuildConfig.VERSION_CODE)
            .enqueue(object : Callback<InitialDataResponse> {
                override fun onResponse(
                    call: Call<InitialDataResponse>,
                    response: Response<InitialDataResponse>) {
                    if(response.isSuccessful){
                        response.body()?.let { initialDataResponse ->
                            if(!initialDataResponse.error){
                                initialDataResponse.initialData?.let {
                                    LocalDB.saveInitialData(it)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<InitialDataResponse>, t: Throwable) {
                    com.maruf.jdialog.JDialog.make(this@MessTypeActivity)
                        .setCancelable(false)
                        .setBodyText("Something went wrong! Please try again.")
                        .setIconType(com.maruf.jdialog.JDialog.IconType.ERROR)
                        .setPositiveButton("Try Again"){
                            it.hideDialog()
                            getInitialData()
                        }.build()
                        .showDialog()
                }

            })
    }
}