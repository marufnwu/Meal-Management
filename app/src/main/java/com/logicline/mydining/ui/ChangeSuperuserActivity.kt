package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityCahngeSuperuserBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.UserListResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeSuperuserActivity : BaseActivity(false) {
    private var selectedUser: User? = null
    private var userList: List<User> = listOf()
    lateinit var binding : ActivityCahngeSuperuserBinding
    lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityCahngeSuperuserBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Change Superuser"

        setContentView(binding.root)



        loadingDialog  = LoadingDialog(this)
        if(!Constant.isSuperUser()){
            binding.txtNonSuperUser.visibility  =View.VISIBLE
            binding.cardSuperuser.visibility = View.GONE
        }else{
            binding.txtNonSuperUser.visibility  =View.GONE
            binding.cardSuperuser.visibility = View.VISIBLE

            binding.spinnerMember.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2>0){
                        userList.let { userList->
                            selectedUser = userList[p2-1]
                        }
                    }else{
                        selectedUser = null
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    selectedUser = null
                }

            }

            binding.btnChange.setOnClickListener {
                change()
            }

            getUsersList()

        }
    }

    private fun change() {
        if (selectedUser==null){
            shortToast("Please select a user first")
            return
        }
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .changeSuperUser(selectedUser!!.id!!)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)
                        if(!response.body()!!.error){
                            startActivity(Intent(this@ChangeSuperuserActivity, FirstActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }


    private fun getUsersList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi.getUsers(1)
            .enqueue(object: Callback<UserListResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            userList  = userListResponse.userList!!
                            setUsersToSpinner(userList)

                        }else{
                            shortToast(response.body()?.msg)
                        }
                    }
                }
                override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
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
}