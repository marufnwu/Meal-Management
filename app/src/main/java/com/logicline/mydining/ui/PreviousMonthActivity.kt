package com.logicline.mydining.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityPreviousMonthBinding
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.whiteelephant.monthpicker.MonthPickerDialog

class PreviousMonthActivity : BaseActivity(), OnClickListener {

    lateinit var myFullScreenAd: MyFullScreenAd
    lateinit var binding : ActivityPreviousMonthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviousMonthBinding.inflate(layoutInflater)
        supportActionBar?.title = getString(R.string.previous_data)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setContentView(binding.root)

        intent?.let {
            it.getStringExtra(Constant.YEAR)?.let {
                year = it
            }

            it.getStringExtra(Constant.MONTH)?.let {
                month = it

            }

            if(month!=null && year!=null){
                setButtonText(year!!, month!!)
            }

            it.getStringExtra(Constant.MONTH_NAME)?.let {
                monthName = it
            }

            it.getIntExtra(Constant.MONTH_ID, 0)?.let {
                if(it!=0){
                    monthId = it
                }
            }

        }

        myFullScreenAd = MyFullScreenAd(this, true)

        binding.deposit.setOnClickListener(this)
        binding.purchases.setOnClickListener(this)
        binding.mealChart.setOnClickListener(this)
        binding.otherCost.setOnClickListener(this)
        binding.summary.setOnClickListener(this)

        binding.btnSelectDate.setOnClickListener {
            MonthPickerDialog.Builder(this, { m, y ->
                year = y.toString()
                month = (m+1).toString()

                setButtonText(year!!, month!!)


            }, Constant.getCurrentYear().toInt(),
                Constant.getCurrentMonthNumber().toInt()-1)
                .setTitle("Select Month")
                .build()
                .show()
        }


    }

    private fun setButtonText(year:String, month:String){
        binding.btnSelectDate.text = Constant.getMonthName("$year-$month-01")+" $year"
    }

    override fun onClick(v: View?) {
        if(year==null || month==null){
            shortToast("Please select a month first")
            return
        }

        when(v?.id){
            R.id.summary -> startActivity(Intent(this, SummaryActivity::class.java)
                .putExtra(Constant.FORCE, true)
                .putExtra(Constant.YEAR, year.toString()).putExtra(Constant.MONTH, month.toString()))
            R.id.deposit -> startActivity(Intent(this, DepositActivity::class.java)
                .putExtra(Constant.FORCE, true)
                .putExtra(Constant.YEAR, year.toString()).putExtra(Constant.MONTH, month.toString()))
            R.id.mealChart -> startActivity(Intent(this, MealActivity::class.java)
                .putExtra(Constant.FORCE, true)
                .putExtra(Constant.YEAR, year.toString()).putExtra(Constant.MONTH, month.toString()))

            R.id.purchases -> startActivity(Intent(this, PurchasesActivity::class.java)
                .putExtra(Constant.FORCE, true)
                .putExtra(Constant.PURCHASE_TYPE, 1)
                .putExtra(Constant.YEAR, year.toString()
                ).putExtra(Constant.MONTH, month.toString()))
            R.id.otherCost -> startActivity(Intent(this, PurchasesActivity::class.java)
                .putExtra(Constant.FORCE, true)

                .putExtra(Constant.PURCHASE_TYPE, 2)
                .putExtra(Constant.YEAR, year.toString())
                .putExtra(Constant.MONTH, month.toString()))
        }
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