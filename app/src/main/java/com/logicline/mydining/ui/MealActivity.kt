package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.DayMealListAdapter
import com.logicline.mydining.databinding.ActivityMealBinding
import com.logicline.mydining.databinding.DialogEditMealDialogBinding
import com.logicline.mydining.models.Meal
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.MealListResponse
import com.logicline.mydining.models.response.UserDayMealResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LangUtils
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MealActivity"
class MealActivity : BaseActivity() {

    lateinit var myFullScreenAd: MyFullScreenAd

    lateinit var adapter: DayMealListAdapter
    lateinit var binding: ActivityMealBinding
    lateinit var loadingDialog: LoadingDialog
    var dayList :  MutableList<MutableList<Meal>> = mutableListOf()



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.meal_chart)

        myFullScreenAd = MyFullScreenAd(this, true)
        loadingDialog = LoadingDialog(this)

        if(!Constant.isManagerOrSuperUser()){
            binding.addMeal.visibility = View.GONE
        }

        intent?.let {
            it.getStringExtra(Constant.YEAR)?.let {
                year = it
            }

            it.getStringExtra(Constant.MONTH)?.let {
                month = it
            }



            it.getIntExtra(Constant.MONTH_ID, 0).let {
                monthId = if(it==0) null else it
            }

            it.getBooleanExtra(Constant.FORCE, false).let {
                force =  it
            }


        }

        binding.monthPicker.builder(null, mYear = year?.toInt(), mMonth = month?.toInt(), mDay = 1, force = force )
            .onDateSelectListener = object : MyDatePicker.OnDateSelectListener {
            override fun date(date: Int?, month: Int?, year: Int?) {
                setDate(year.toString(), month.toString())
            }

            override fun dateString(date: String) {

            }

            override fun month(id: Int) {
                monthId = id
                year = null
                month =null
                getMealList()
            }

        }
        if(!force){
            if(Constant.getMessType() == Constant.MessType.MANUALLY){
                monthId = Constant.getMonthId()
            }else{
                year = Constant.getCurrentYear()
                month = Constant.getCurrentMonthNumber()
            }
        }


        binding.addMeal.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }



        val recyDayMeal = binding.recyDayList
        recyDayMeal.setHasFixedSize(true)
        recyDayMeal.layoutManager =LinearLayoutManager(this@MealActivity)

        adapter = DayMealListAdapter(this@MealActivity, dayList)
        adapter.onAction = object: DayMealListAdapter.OnAction {
            override fun onClick(meal: Meal, pos: Int) {
                showEditMealDialog(meal)
            }

        }
        recyDayMeal.adapter = adapter
        getMealList()
    }

    private fun showEditMealDialog(meal: Meal) {
        val editBinding = DialogEditMealDialogBinding.inflate(layoutInflater)


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

        editBinding.txtName.text = meal.name
        editBinding.txtDate.text = meal.date

        editBinding.edtBreakfast.setText(meal.breakfast)
        editBinding.edtLunch.setText(meal.lunch)
        editBinding.edtDinner.setText(meal.dinner)

        editBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        editBinding.txtDate.setOnClickListener {
            MyDatePicker(
                this,
                object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int?, month: Int?, year: Int?) {

                    }

                    override fun dateString(date: String) {
                        meal.date = date
                        editBinding.txtDate.text = date
                    }

                },
                Constant.getDay(meal.date!!).toInt(),
                Constant.getMonthNumber(meal.date!!).toInt(),
                Constant.getYear(meal.date!!).toInt(),
            ).create().show()
        }

        editBinding.btnUpdate.setOnClickListener {

            val breakFastStr = editBinding.edtBreakfast.text.toString()
            val lunchStr = editBinding.edtLunch.text.toString()
            val dinnerStr = editBinding.edtDinner.text.toString()

            if(meal.date.isNullOrEmpty()){
                shortToast("Please select date");
                return@setOnClickListener
            }

            if(dinnerStr.isEmpty() || lunchStr.isEmpty() || breakFastStr.isEmpty()){
                Toast.makeText(this, "Some field not valid", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val breakfast = breakFastStr.toFloat()
            val lunch = lunchStr.toFloat()
            val dinner = dinnerStr.toFloat()

            if(breakfast<0 || lunch <0 || dinner<0){
                Toast.makeText(this, "Some field not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            updateMeal(meal.id!!, meal.userId!!, meal.date!!, breakfast, lunch, dinner, dialog)
        }

        editBinding.btnDelete.setOnClickListener {
            deleteMeal(meal.id!!, dialog)
        }

        dialog.show()
    }

    private fun deleteMeal(id: String, dialog: Dialog) {
        loadingDialog.hide()
        (application as MyApplication)
            .myApi
            .deleteMeal(id)
            .enqueue(object: Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        Toast.makeText(this@MealActivity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                        if(!response.body()!!.error){
                            dialog.dismiss()
                            getMealList()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun updateMeal(mealId:String, userId: String, date: String, breakfast: Float, lunch: Float, dinner: Float, dialog: Dialog) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .updateMeal(mealId, userId, date, breakfast, lunch, dinner)
            .enqueue(object: Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        Toast.makeText(this@MealActivity, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                        if(!response.body()!!.error){
                            dialog.dismiss()
                            getMealList()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })



    }


    override fun onStart() {
        super.onStart()
        getMealList()

    }

    private fun getMealList() {
        if(monthId==null && year==null && month ==null){
            return
        }
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getMealByMonth(year, month, monthId)
            .enqueue(object : Callback<MealListResponse> {
                override fun onResponse(call: Call<MealListResponse>, response: Response<MealListResponse>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){

                        binding.txtTotalMeal.text = "Total Meal "+response.body()?.totalMeal.toString()

                        dayList.clear()
                        response.body()!!.meals?.let { dayList.addAll(it) }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<MealListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setDate(year:String, month:String){
        this.year = year
        this.month = month
        this.monthId= null
        getMealList()
    }
    override fun onBackPressed() {
        myFullScreenAd.showAd()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase!=null) {
            super.attachBaseContext(LangUtils.applyLanguage(newBase))
        } else {
            super.attachBaseContext(newBase)
        }
    }
}