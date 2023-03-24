package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.UserInitiateAdapter
import com.logicline.mydining.databinding.ActivityInitiateMemberBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.Coroutines
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast


class InitiateMemberActivity : AppCompatActivity() {
    lateinit var binding : ActivityInitiateMemberBinding
    lateinit var initiateUserAdapter: UserInitiateAdapter
    lateinit var notInitiateUserAdapter: UserInitiateAdapter

    val initiateUser : MutableList<User> = mutableListOf()
    val notInitiateUser : MutableList<User> = mutableListOf()

    lateinit var loadingDialog: LoadingDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitiateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        loadingDialog = LoadingDialog(this)

        initViews()
        getInitiateUser()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.txtMonthYear.text = Constant.getCurrentMonthName()+" "+ Constant.getCurrentYear()

        binding.recyInitiate.layoutManager = LinearLayoutManager(this)
        binding.recyInitiate.setHasFixedSize(true)

        binding.recyNotInitiate.layoutManager = LinearLayoutManager(this)
        binding.recyNotInitiate.setHasFixedSize(true)

        initiateUserAdapter = UserInitiateAdapter(this, initiateUser, UserInitiateAdapter.Type.INITIATE)
        notInitiateUserAdapter = UserInitiateAdapter(this, notInitiateUser, UserInitiateAdapter.Type.NOT_INITIATE)

        notInitiateUserAdapter.setOnActionClick(object : UserInitiateAdapter.OnActionClick {
            override fun onClick(user: User) {
                initiateUserNow(user)
            }

        })



        binding.recyInitiate.adapter = initiateUserAdapter
        binding.recyNotInitiate.adapter = notInitiateUserAdapter
    }

    private fun initiateUserNow(user: User) {
        loadingDialog.show()
        Coroutines.main {
            try {
                val res = (application as MyApplication)
                    .myApi
                    .initiateUser(user.id!!)

                loadingDialog.hide()

                if(res.isSuccessful && res.body()!=null){
                    if(!res.body()!!.error){
                        getInitiateUser()
                    }else{
                        shortToast(res.body()!!.msg)
                    }
                }

            }catch (e:Exception){
                loadingDialog.hide()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getInitiateUser() {
        Coroutines.main {
            try {
                val res = (application as MyApplication).myApi.getUsersForInitiate()
                if(res.isSuccessful && res.body()!=null){
                    val r = res.body()!!

                    if(!r.error){
                        r.data?.let {
                            it.initiatedUsers?.let {
                                if(it.size>0){
                                    binding.recyInitiate.visibility  =View.VISIBLE
                                    binding.txtInitiate.visibility = View.GONE

                                    initiateUser.clear()
                                    initiateUser.addAll(it)
                                    initiateUserAdapter.notifyDataSetChanged()
                                }else{
                                    binding.recyInitiate.visibility  =View.GONE
                                    binding.txtInitiate.visibility = View.VISIBLE
                                }
                            }

                            it.users?.let {
                                if(it.size>0){

                                    binding.recyNotInitiate.visibility  =View.VISIBLE
                                    binding.txtNotInitiate.visibility = View.GONE

                                    notInitiateUser.clear()
                                    notInitiateUser.addAll(it)
                                    notInitiateUserAdapter.notifyDataSetChanged()
                                }else{
                                    binding.recyNotInitiate.visibility  =View.GONE
                                    binding.txtNotInitiate.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                }
            }catch (e:Exception){

            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}