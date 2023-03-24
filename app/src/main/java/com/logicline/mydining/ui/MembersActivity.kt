package com.logicline.mydining.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.adapter.UserListAdapter
import com.logicline.mydining.databinding.ActivityMembersBinding
import com.logicline.mydining.databinding.DialogAddNewMemberBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.UserListResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.maruf.jdialog.JDialog

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.random.Random

class MembersActivity : AppCompatActivity() {
    lateinit var adapter: UserListAdapter
    lateinit var binding : ActivityMembersBinding
    var user: User? = null
    lateinit var loadingDialog: LoadingDialog
    private var userList: MutableList<User> = mutableListOf<User>()
    lateinit var myFullScreenAd: MyFullScreenAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog = LoadingDialog(this)
        user = LocalDB.getUser()

        if(user?.accType== Constant.NORMAL_USER){
            binding.fab.visibility = View.GONE
        }

        binding.recyMembers.setHasFixedSize(true)
        binding.recyMembers.layoutManager = LinearLayoutManager(this@MembersActivity)

        adapter = UserListAdapter(this, userList)

        adapter.onAction = object : UserListAdapter.OnAction {
            override fun onDeleteClick(userId: String) {
                userDeleteCheck(userId)
            }

        }
        binding.recyMembers.adapter = adapter

        binding.fab.setOnClickListener {
            showAddMemberDialog()
        }

        getUsersList()
    }

    private fun userDeleteCheck(userId: String) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .userDeleteCheck(userId, Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val body = response.body()!!
                        if(body.error){
                            //show warning

                            JDialog.make(this@MembersActivity)
                                .setCancelable(true)
                                .setIconType(JDialog.IconType.WARNING)
                                .setBodyText(body.msg)
                                .setNegativeButton("Cancel"){
                                    it.hideDialog()
                                }.setPositiveButton("Yes! Delete Now"){
                                    it.hideDialog()
                                    deleteUser(userId)
                                }
                                .build()
                                .showDialog()

                        }else{
                            //account deleted

                            shortToast(body.msg)
                            getUsersList()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun deleteUser(userId: String) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .userDelete(userId, Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()

                    if (response.isSuccessful && response.body()!=null){
                        val body = response.body()!!
                        shortToast(body.msg)
                        if(!body.error){
                            getUsersList()
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

        dialogBinding.ccp.setAutoDetectedCountry(true)
        dialogBinding.ccp.setNumberAutoFormattingEnabled(true)
        dialogBinding.ccp.setHintExampleNumberEnabled(true)
        dialogBinding.ccp.registerCarrierNumberEditText(dialogBinding.edtPhone)

        dialogBinding.btnGenPass.setOnClickListener {
            val pass = Random.nextInt(111111, 999999).toString()
            dialogBinding.edtPass.setText(pass)
        }


        ArrayAdapter.createFromResource(this, R.array.gender, R.layout.layout_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dialogBinding.spinnerGender.adapter = adapter
        }

        var gender : String? = null
        dialogBinding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position>0){
                    gender = parent?.getItemAtPosition(position).toString()
                }else{
                    gender= null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        dialogBinding.btnSubmit.setOnClickListener {
            val name = dialogBinding.edtName.text.toString()
            val pass = dialogBinding.edtPass.text.toString()
            val userName = dialogBinding.edtUerName.text.toString()

            var email = dialogBinding.edtEmail.text.toString()
            val city = dialogBinding.edtCity.text.toString()

            val country = dialogBinding.ccp.selectedCountryNameCode


            if(country==null){
                shortToast("Please select country first.")
                return@setOnClickListener
            }


            if(!dialogBinding.ccp.isValidFullNumber){
                shortToast("Please enter valid mobile number")
                return@setOnClickListener
            }

            val phone = dialogBinding.ccp.fullNumber

            if(gender==null){
                shortToast("Please select gender")
                return@setOnClickListener
            }


            if(name.isEmpty() || pass.isEmpty() || phone.isEmpty() || city.isEmpty() ){
                Toast.makeText(this, "All Fields Are Required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pass.length<6){
                shortToast("Password must garter than 5 character")
                return@setOnClickListener
            }

            if(email.isEmpty()){
                email="";
            }

            loadingDialog.show()
            (application as MyApplication).myApi
                .addUser(name, phone, pass, userName, email, city, gender!!, country)
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