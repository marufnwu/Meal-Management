package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.DayMealListAdapter
import com.logicline.mydining.databinding.ActivityMealBinding
import com.logicline.mydining.databinding.DialogEditMealDialogBinding
import com.logicline.mydining.models.Meal
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.MealListResponse
import com.logicline.mydining.models.response.UserDayMealResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealActivity : AppCompatActivity(), MyDatePicker.OnDateSelectListener {

    lateinit var myFullScreenAd: MyFullScreenAd

    lateinit var adapter: DayMealListAdapter
    lateinit var binding: ActivityMealBinding
    lateinit var loadingDialog: LoadingDialog
    var dayList :  MutableList<MutableList<Meal>> = mutableListOf()
    var year = Constant.getCurrentYear()
    var month = Constant.getCurrentMonthNumber()
    var day = Constant.getCurrentDay()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myFullScreenAd = MyFullScreenAd(this, true)
        loadingDialog = LoadingDialog(this)

        if(!Constant.isManagerOrSuperUser()){
            binding.addMeal.visibility = View.GONE
        }

        binding.addMeal.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }

        binding.month.setOnClickListener {
            selectMonth()
        }

        binding.month.text  = Constant.getYear(Constant.getCurrentDate())+" "+Constant.getMonthName(Constant.getCurrentDate())

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
                    override fun date(date: Int, month: Int, year: Int) {

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

    private fun selectMonth() {
        MyDatePicker(this, this, day.toInt(), month.toInt(), year.toInt())
            .create()
            .show()
    }

    private fun getMealList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getMealByMonth(year, month)
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

    override fun date(date: Int, month: Int, year: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun dateString(date: String) {
        binding.month.text = Constant.getMonthName(date)+" "+Constant.getYear(date)
        year = Constant.getYear(date)
        month = Constant.getMonthNumber(date)
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
}