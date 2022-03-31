package com.maruf.messmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.maruf.messmanagement.databinding.ActivityMainBinding
import com.maruf.messmanagement.models.User
import com.maruf.messmanagement.models.response.HomeDataResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import com.maruf.messmanagement.utils.MyExtensions.shortToast
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var user : User? = null
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)
        user = Paper.book().read(Constant.userKey)
        if(!Constant.isManagerOrSuperUser()){
            binding.addMeal.visibility = View.GONE
        }

        initListener()

        getHomeData()


    }

    private fun getHomeData() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .getHomeData()
            .enqueue(object : Callback<HomeDataResponse> {
                override fun onResponse(
                    call: Call<HomeDataResponse>,
                    response: Response<HomeDataResponse>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful){
                        response.body()?.let { homeDataResponse ->
                            if(!homeDataResponse.error){
                                homeDataResponse.home?.let {
                                    home ->
                                    binding.txtMealcharge.text = home.mealCharge
                                    binding.txtTotalMeal.text = home.totalMeal
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                    shortToast(t.message)
                    loadingDialog.hide()
                }

            })
    }

    private fun initListener() {
        binding.addMember.setOnClickListener {
            startActivity(Intent(this, MembersActivity::class.java))
        }

        binding.mealChart.setOnClickListener {
            startActivity(Intent(this, MealActivity::class.java))
        }

        binding.purchases.setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java).putExtra(Constant.PURCHASE_TYPE, 1))
        }

        binding.otherCost.setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java).putExtra(Constant.PURCHASE_TYPE, 2))
        }

        binding.addMeal.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }


        binding.deposit.setOnClickListener {
            startActivity(Intent(this, DepositActivity::class.java))
        }

        binding.summary.setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).putExtra("profile",user))

        }
    }


}