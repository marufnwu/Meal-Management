package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.os.Bundle

import android.app.DatePickerDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityAddMealBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.UserDayMealResponse
import com.logicline.mydining.models.response.UserListResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AddMealActivity : BaseActivity(true) , AdapterView.OnItemSelectedListener {

    private lateinit var myFullScreenAd: MyFullScreenAd

    private var userList: List<User>? = null
    lateinit var binding : ActivityAddMealBinding
    lateinit var loadingDialog : LoadingDialog
    var selectedDate : String?  =null
    var memberArray = arrayListOf<User>()
    var selectedUser : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.add_meal)

        myFullScreenAd = MyFullScreenAd(this, true)
        loadingDialog = LoadingDialog(this)




        if(!Constant.isManagerOrSuperUser()){
            binding.txtInstruction.text = "You are regular user, so you can only request today and tomorrow meal"
        }



        initView()

        //MediationTestSuite.launch(this);


    }

    override fun onResume() {
        super.onResume()
        getUsersList()
    }


    private fun getUserListForManager(){
        if(selectedDate == null){
            shortToast("Auto date selection not working, please contact customer support.")
            return
        }


        loadingDialog.show()
        (application as MyApplication)
            .myApi.getInitiatedUsers(1, selectedDate!!)
            .enqueue(object: Callback<UserListResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            userList  = userListResponse.userList!!
                            setUsersToSpinner(userList!!)

                        }else{
                            Toast.makeText(this@AddMealActivity, response.body()?.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }
    private fun getUserListFoRegularUser(){
        if(selectedDate == null){
            shortToast("Auto date selection not working, please contact customer support.")
            return
        }


        loadingDialog.show()
        (application as MyApplication)
            .myApi.currentInitiatedUser( selectedDate!!)
            .enqueue(object: Callback<UserListResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            userList  = userListResponse.userList!!
                            setUsersToSpinner(userList!!)

                        }else{
                            Toast.makeText(this@AddMealActivity, response.body()?.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun getUsersList() {

        if(Constant.isManagerOrSuperUser()){
            getUserListForManager()
        }else{
            LocalDB.getUser()?.let {
                if(it.allUserAddMeal==1){
                    getUserListFoRegularUser()
                }
            }
        }

    }

    private fun setUsersToSpinner(userList: List<User>) {
        val usersArray = arrayListOf<String?>()
        usersArray.add("Select Uer")
        userList.listIterator().forEach { member->
            usersArray.add(member.name)
            Log.d("Member", member.name!!)
        }

        Log.d("Member", usersArray.size.toString())


        ArrayAdapter(this,  android.R.layout.simple_spinner_item, usersArray)
            .also { adapter->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerMember.adapter = adapter


            }

    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        selectedDate = Constant.getCurrentDate()




        binding.txtMonthYear.text = Constant.getCurrentMonthName()+" "+Constant.getCurrentYear()
        binding.txtDate.text = Constant.getCurrentDate()+" "+Constant.getCurrentDayName()
        binding.spinnerMember.onItemSelectedListener = this

        binding.btnAddMeal.setOnClickListener {
            addMeal()
        }

        binding.txtDate.setOnClickListener {
            if(Constant.isManagerOrSuperUser()){
                showDateTimePicker()
            }else{
                showRangeDatePicker()
            }
        }

    }

    private fun showRangeDatePicker() {
        // Get Current Date
        // Get Current Date
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            selectedDate = Constant.dateFormat(date)
            binding.txtDate.text = selectedDate+" "+Constant.getDayNameFromDate(selectedDate!!)
            binding.txtMonthYear.text = Constant.getMonthName(date)+" "+Constant.getYear(date)
            getUsersList()

        }, mYear, mMonth, mDay)

        c.add(Calendar.DATE, 1)
        datePickerDialog.datePicker.maxDate = c.timeInMillis
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()


        datePickerDialog.show()


    }

    private fun addMeal() {
        val breakFastStr = binding.edtBreakfast.text.toString()
        val lunchStr = binding.edtLunch.text.toString()
        val dinnerStr = binding.edtDinner.text.toString()

        if(selectedDate==null){
            shortToast("Please select date");
            return
        }

        if (selectedUser==null){
            shortToast("Please select purchase user name")
            return
        }

        if(dinnerStr.isEmpty() || lunchStr.isEmpty() || breakFastStr.isEmpty()){
            Toast.makeText(this, "Some field not valid", Toast.LENGTH_SHORT).show()

            return
        }

        val breakfast = breakFastStr.toFloat()
        val lunch = lunchStr.toFloat()
        val dinner = dinnerStr.toFloat()

        if(breakfast<0 || lunch <0 || dinner<0){
            Toast.makeText(this, "Some field not valid", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .addMeal(selectedUser?.id!!, selectedDate!!, breakfast, lunch, dinner)
            .enqueue(object: Callback<UserDayMealResponse> {
                override fun onResponse(
                    call: Call<UserDayMealResponse>, response: Response<UserDayMealResponse>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        val dayMealResponse = response.body()!!
                        Toast.makeText(this@AddMealActivity, dayMealResponse.msg, Toast.LENGTH_SHORT).show()
                        if(!dayMealResponse.error){

                            if(dayMealResponse.meal!=null){
                                val meal = dayMealResponse.meal!!
                                binding.edtDinner.setText(meal.dinner)
                                binding.edtLunch.setText(meal.lunch)
                                binding.edtBreakfast.setText(meal.breakfast)
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<UserDayMealResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })



    }

    @SuppressLint("SetTextI18n")
     fun showDateTimePicker() {
        // Get Current Date
        // Get Current Date
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            selectedDate = Constant.dateFormat(date)
            binding.txtDate.text = selectedDate+" "+Constant.getDayNameFromDate(selectedDate!!)
            binding.txtMonthYear.text = Constant.getMonthName(date)+" "+Constant.getYear(date)
            getUsersList()

        }, mYear, mMonth, mDay)


        datePickerDialog.show()

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        if(p2>0){
            userList?.let { userList->
                selectedUser = userList[p2-1]

                selectedUser?.let {
                    getUserMealByDate()
                }
            }
        }else{
            selectedUser = null
        }

    }

    private fun getUserMealByDate() {
        loadingDialog.show()
        if(selectedDate.isNullOrEmpty()){
            Toast.makeText(this, "Please Select Date", Toast.LENGTH_SHORT).show()
            return
        }

        if(selectedUser !=null){
            (application as MyApplication)
                .myApi
                .getUserMealByDate(selectedUser?.id!!, selectedDate!!)
                .enqueue(object : Callback<UserDayMealResponse> {
                    override fun onResponse(
                        call: Call<UserDayMealResponse>, response: Response<UserDayMealResponse>) {
                        loadingDialog.hide()
                        if(response.isSuccessful && response.body()!=null){
                            val dayMealResponse = response.body()!!
                            if(!dayMealResponse.error){

                                if(dayMealResponse.meal!=null){
                                    val meal = dayMealResponse.meal!!
                                    binding.edtDinner.setText(meal.dinner)
                                    binding.edtLunch.setText(meal.lunch)
                                    binding.edtBreakfast.setText(meal.breakfast)
                                }else{
                                    binding.edtDinner.setText("0")
                                    binding.edtLunch.setText("0")
                                    binding.edtBreakfast.setText("0")
                                }

                            }
                        }
                    }

                    override fun onFailure(call: Call<UserDayMealResponse>, t: Throwable) {
                        loadingDialog.hide()
                    }

                })
        }else{
            Toast.makeText(this, "Please select a member", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

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