package com.maruf.messmanagement.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.maruf.messmanagement.R
import com.maruf.messmanagement.adapter.UserListAdapter
import com.maruf.messmanagement.databinding.ActivityMembersBinding
import com.maruf.messmanagement.databinding.DialogAddNewMemberBinding
import com.maruf.messmanagement.models.User
import com.maruf.messmanagement.models.response.GenericRespose
import com.maruf.messmanagement.models.response.UserListResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.LoadingDialog
import com.maruf.messmanagement.utils.MyApplication
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class MembersActivity : AppCompatActivity() {
    lateinit var adapter: UserListAdapter
    lateinit var binding : ActivityMembersBinding
    lateinit var user: User
    lateinit var loadingDialog: LoadingDialog
    private var userList: MutableList<User> = mutableListOf<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)
        user = Paper.book().read<User>(Constant.userKey)!!

        Log.d("sss", user.accType!!)

        if(user.accType==Constant.NORMAL_USER){
            binding.fab.visibility = View.GONE
        }

        binding.recyMembers.setHasFixedSize(true)
        binding.recyMembers.layoutManager = LinearLayoutManager(this@MembersActivity)

        adapter = UserListAdapter(this, userList)
        binding.recyMembers.adapter = adapter

        binding.fab.setOnClickListener {
            showAddMemberDialog()
        }

        getUsersList()
    }

    private fun getUsersList() {
        loadingDialog.show()
        (application as MyApplication)
            .myApi.getUsers(0)
            .enqueue(object: Callback<UserListResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<UserListResponse>, response: Response<UserListResponse>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            val list = userListResponse.userList!!
                            setUsers(list)

                        }
                    }
                }
                override fun onFailure(call: Call<UserListResponse>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setUsers(list: List<User>) {
        userList.clear()
        userList.addAll(list)
        adapter.notifyDataSetChanged()

        binding.txtMember.text = list.size.toString()
    }

    private fun showAddMemberDialog() {

        val dialogBinding = DialogAddNewMemberBinding.inflate(layoutInflater);

        val builder= AlertDialog.Builder(this)
            .setCancelable(true)
            .setView(dialogBinding.root)

        var addMemberDialog=builder.create()

        dialogBinding.btnGenPass.setOnClickListener {
            val pass = Random.nextInt(111111, 999999).toString()
            dialogBinding.edtPass.setText(pass)
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val name = dialogBinding.edtName.text.toString()
            val pass = dialogBinding.edtPass.text.toString()
            val phone = dialogBinding.edtPhone.text.toString()

            if(name.isEmpty() || pass.isEmpty() || phone.isEmpty()){
                Toast.makeText(this, "Incorrect Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadingDialog.show()
            (application as MyApplication).myApi
                .addUser(name, phone, pass)
                .enqueue(object: Callback<GenericRespose> {
                    override fun onResponse(
                        call: Call<GenericRespose>, response: Response<GenericRespose>
                    ) {
                        loadingDialog.hide()
                        if(response.isSuccessful && response.body()!=null){

                            Toast.makeText(this@MembersActivity, response.body()!!.msg, Toast.LENGTH_SHORT).show()

                            if(!response.body()!!.error){
                                addMemberDialog.dismiss()
                                getUsersList()
                            }

                        }
                    }

                    override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                        loadingDialog.hide()

                    }

                })

        }




        addMemberDialog.show()
    }
}