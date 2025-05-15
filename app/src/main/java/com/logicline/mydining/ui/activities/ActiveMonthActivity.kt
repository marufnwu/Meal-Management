package com.logicline.mydining.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.logicline.mydining.R
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.databinding.ActivityActiveMonthBinding
import com.logicline.mydining.ui.adapter.MonthAdapter
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.MyApplication
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.ui.custom.monthpicker.MonthPickerDialog
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast

class ActiveMonthActivity : BaseActivity() {
    private lateinit var binding : ActivityActiveMonthBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var adapter: MonthAdapter
    private var months: MutableList<Month> = mutableListOf()
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


//        MonthPickerDialog.show(
//            context = this,
//            monthRepository = MonthRepository((application as MyApplication).myApi),
//            preselectedMonthId = 5
//        )  { selectedMonth ->
//            Toast.makeText(this, "Selected: ${selectedMonth.name}", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun initViews() {
       binding.monthPicker.initialize(MonthRepository((application as MyApplication).myApi)){month->
           shortToast("Selected: ${month.name}")
           AppPrefs.monthId = month.id
       }

       lifecycleScope.launchWhenStarted {
           AppPrefs.monthIdFlow.collect {
               shortToast("Selected: ${it}")
                binding.monthPicker.setSelectedMonthId(it)
           }
       }

    }

//    private fun getMonthList() {
//        loadingDialog.show()
//        (application as MyApplication)
//            .myApi
//            .getMonths()
//            .enqueue(object : Callback<ServerResponse<MutableList<Month>>> {
//                override fun onResponse(
//                    call: Call<ServerResponse<MutableList<Month>>>,
//                    response: Response<ServerResponse<MutableList<Month>>>
//                ) {
//                    loadingDialog.hide()
//                    if (response.isSuccessful && response.body()!=null){
//                        if(!response.body()!!.error){
//                            months.addAll(response.body()!!.data!!)
//                            adapter.notifyDataSetChanged()
//                            binding.v.setItems(months.map { it.name })
//
//                        }
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<ServerResponse<MutableList<Month>>>,
//                    t: Throwable
//                ) {
//                    loadingDialog.hide()
//                }
//
//            })
//    }

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