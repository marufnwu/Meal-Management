package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.ReportAdapter
import com.logicline.mydining.databinding.ActivityReportBinding
import com.logicline.mydining.databinding.DialogNewReportGenerateLayoutBinding
import com.logicline.mydining.models.Report
import com.logicline.mydining.models.response.Paging
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.whiteelephant.monthpicker.MonthPickerDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReportActivity : BaseActivity(false) {
    lateinit var myFullScreenAd: MyFullScreenAd

    lateinit var binding: ActivityReportBinding
    lateinit var layoutManager: LinearLayoutManager
    lateinit var loadingDialog: LoadingDialog

    var pastVisibleItem: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0


    private var CURR_PAGE = 1
    private var TOTAL_PAGE = 0
    var isLoading = true

    private var reports = mutableListOf<Report>()
    private lateinit var adapter: ReportAdapter

    private var selectedMonth : Int?  = null
    private var selectedYear : Int?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Generated Report"
        setContentView(binding.root)

        myFullScreenAd = MyFullScreenAd(this, true)


        loadingDialog = LoadingDialog(this)

        initViews()
        getReport(CURR_PAGE, TOTAL_PAGE)

    }

    private fun getReport(currPage: Int, totalPage: Int) {
        if (currPage == 1) {
            loadingDialog.show()
        }

        (application as MyApplication)
            .myApi
            .getAllReport(currPage, totalPage)
            .enqueue(object : Callback<ServerResponse<Paging<Report>>> {
                override fun onResponse(
                    call: Call<ServerResponse<Paging<Report>>>,
                    response: Response<ServerResponse<Paging<Report>>>
                ) {
                    isLoading = false
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.error) {
                            response.body()!!.data?.let {


                                if (it.data!!.size > 0) {
                                    binding.layoutEmpty.root.visibility = View.GONE

                                    reports.addAll(it.data!!)
                                    adapter.notifyDataSetChanged()
                                    TOTAL_PAGE = it.totalPage

                                } else {
                                    if (currPage == 1) {
                                        binding.layoutEmpty.root.visibility = View.VISIBLE
                                        binding.recyReport.visibility = View.GONE
                                    }
                                }

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Paging<Report>>>, t: Throwable) {
                    isLoading = false
                    loadingDialog.hide()
                }

            })
    }

    private fun initViews() {
        binding.btnGenerate.setOnClickListener {
            showDialog()
        }
        binding.recyReport.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.recyReport.layoutManager = layoutManager

        adapter = ReportAdapter(this, reports)
        binding.recyReport.adapter = adapter


    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {

        selectedMonth = null
        selectedYear = null


        val dialogBinding = DialogNewReportGenerateLayoutBinding.inflate(layoutInflater)


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogBinding.root)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        dialogBinding.txtMonth.setOnClickListener {
            val  builder = MonthPickerDialog.Builder(this, { m, y ->
                selectedMonth = m+1
                selectedYear = y


                dialogBinding.txtMonth.text = Constant.getMonthName("${selectedYear}-${selectedMonth}-01")+" "+selectedYear


            },Constant.getCurrentYear().toInt(), Constant.getCurrentMonthNumber().toInt()-1)

            builder.setTitle("Select Month")
                .build()
                .show()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialogBinding.btnGenerate.setOnClickListener {
            if(selectedMonth==null || selectedYear==null){
                shortToast("Please select month")
                return@setOnClickListener
            }

            loadingDialog.show()
            (application as MyApplication)
                .myApi
                .genereteFullReport(selectedYear!!, selectedMonth!!)
                .enqueue(object : Callback<ServerResponse<Report>> {
                    override fun onResponse(
                        call: Call<ServerResponse<Report>>,
                        response: Response<ServerResponse<Report>>
                    ) {
                        loadingDialog.hide()
                        if(response.isSuccessful && response.body()!=null){
                            shortToast(response.body()!!.msg)
                            if(!response.body()!!.error){
                                dialog.dismiss()
                                response.body()!!.data?.let {
                                    addLatestReport(it)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ServerResponse<Report>>, t: Throwable) {
                        loadingDialog.hide()
                    }

                })
        }

        dialog.show()

    }

    private fun addLatestReport(it: Report) {
        val newList :MutableList<Report> = mutableListOf()
        newList.add(it)
        newList.addAll(reports)

        reports.clear()
        reports.addAll(newList)
        adapter.isAnimate = true
        adapter.notifyDataSetChanged()
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