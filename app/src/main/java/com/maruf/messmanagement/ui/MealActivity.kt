package com.maruf.messmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.maruf.messmanagement.adapter.DayMealListAdapter
import com.maruf.messmanagement.databinding.ActivityAddMealBinding
import com.maruf.messmanagement.databinding.ActivityMealBinding
import com.maruf.messmanagement.models.response.MealListResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealActivity : AppCompatActivity() {
    lateinit var binding: ActivityMealBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        if(!Constant.isManagerOrSuperUser()){
            binding.addMeal.visibility = View.GONE
        }

        binding.addMeal.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }

        getMealList();
    }

    private fun getMealList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getMealByMonth("2022", "03")
            .enqueue(object : Callback<MealListResponse> {
                override fun onResponse(call: Call<MealListResponse>, response: Response<MealListResponse>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){

                        binding.txtTotalMeal.text = "Total Meal "+response.body()?.totalMeal.toString()

                        val recyDayMeal = binding.recyDayList
                        recyDayMeal.setHasFixedSize(true)
                        recyDayMeal.layoutManager =LinearLayoutManager(this@MealActivity)

                        val adapter = DayMealListAdapter(this@MealActivity, response.body()?.meals!!)
                        recyDayMeal.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<MealListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }
}