package com.logicline.mydining.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.MonthAdapter
import com.logicline.mydining.databinding.ActivityActiveMonthBinding
import com.logicline.mydining.models.MonthOfYear
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActiveMonthActivity : BaseActivity() {
    private lateinit var binding : ActivityActiveMonthBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var adapter: MonthAdapter
    private var months: MutableList<MonthOfYear> = mutableListOf()
    lateinit var myFullScreenAd: MyFullScreenAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.month_list)
        binding = ActivityActiveMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog  = LoadingDialog(this)

        initViews()

        getMonthList()
    }

    private fun initViews() {
        adapter = MonthAdapter(this, months)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        adapter.onClick = {
            startActivity(Intent(this, PreviousMonthActivity::class.java)
                .putExtra(Constant.YEAR, it.year)
                .putExtra(Constant.MONTH, it.month.toString()))
        }
    }

    private fun getMonthList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getActiveMonthList()
            .enqueue(object : Callback<ServerResponse<MutableList<MonthOfYear>>> {
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<MonthOfYear>>>,
                    response: Response<ServerResponse<MutableList<MonthOfYear>>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            months.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<MutableList<MonthOfYear>>>,
                    t: Throwable
                ) {
                    loadingDialog.hide()
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