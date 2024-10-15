package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.R
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
import com.logicline.mydining.utils.MyDownloadManager
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.maruf.jdialog.JDialog
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
        supportActionBar?.title = getString(R.string.generate_report)

        setContentView(binding.root)

        myFullScreenAd = MyFullScreenAd(this, true)


        loadingDialog = LoadingDialog(this)

        //checkStoragePermission()

        initViews()
        getReport(CURR_PAGE, TOTAL_PAGE)

    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                shortToast("Permission granted. You can download report")
            } else {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    JDialog.make(this)
                        .setCancelable(true)
                        .setPositiveButton("Go To Settings"){
                            it.hideDialog()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            val uri = Uri.fromParts("package", packageName, null);
                            intent.data = uri;
                            startActivity(intent)

                        }.setNegativeButton(getString(R.string.cancel)){
                            it.hideDialog()
                        }.setBodyText("For downloading report you must provide access storage permission. You are previously denied permission so go to setting and enable storage access permission.")
                        .build()
                        .showDialog()
                }

            }
        }

    private fun checkStoragePermission() {
        when {ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("ReadPermission", "Granted")
        }
            ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
                Log.d("ReadPermission", "shouldShowRequestPermissionRationale")
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.


                    Log.d("ReadPermission", "Not Granted")
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)




            }
        }
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
        adapter.onAction = {
            if(Constant.checkWriteStoragePermission(this)){
                MyDownloadManager.download(BuildConfig.BASE_URL+it.pdf, it.pdf,  this)
            }else{
                checkStoragePermission()
            }
        }
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
//            val  builder = MonthPickerDialog.Builder(this, { m, y ->
//                selectedMonth = m+1
//                selectedYear = y
//
//
//                dialogBinding.txtMonth.text = Constant.getMonthName("${selectedYear}-${selectedMonth}-01")+" "+selectedYear
//
//
//            },Constant.getCurrentYear().toInt(), Constant.getCurrentMonthNumber().toInt()-1)
//
//            builder.setTitle("Select Month")
//                .build()
//                .show()
            createDialogWithoutDateField()
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

    private fun createDialogWithoutDateField(): DatePickerDialog {
        val dpd = DatePickerDialog(this, null, 2014, 1, 24)
        try {
            val datePickerDialogFields = dpd.javaClass.declaredFields
            for (datePickerDialogField in datePickerDialogFields) {
                if (datePickerDialogField.name == "mDatePicker") {
                    datePickerDialogField.isAccessible = true
                    val datePicker = datePickerDialogField[dpd] as DatePicker
                    val datePickerFields = datePickerDialogField.type.declaredFields
                    for (datePickerField in datePickerFields) {
                        if ("mDaySpinner" == datePickerField.name) {
                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField[datePicker]
                            (dayPicker as View).visibility = View.GONE
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        return dpd
    }

}