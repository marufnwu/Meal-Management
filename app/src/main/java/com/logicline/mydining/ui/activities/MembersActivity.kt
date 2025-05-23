package com.logicline.mydining.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.UserListAdapter
import com.logicline.mydining.databinding.ActivityMembersBinding
import com.logicline.mydining.databinding.DialogAddNewMemberBinding
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.models.Country
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.response.GenericRespose
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.Ad.MyFullScreenAd
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.MyApplication
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MembersActivity : BaseActivity() {
    lateinit var adapter: UserListAdapter
    lateinit var binding : ActivityMembersBinding
    lateinit var loadingDialog: LoadingDialog
    private var userList: MutableList<MessUser> = mutableListOf()
    lateinit var myFullScreenAd: MyFullScreenAd
    var countries : MutableList<Country> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.members)

        myFullScreenAd = MyFullScreenAd(this, true)

        loadingDialog = LoadingDialog(this)

        if(!AppPrefs.messUser.hasAnyPermission(MessPermission.USER_ADD, MessPermission.USER_MANAGEMENT)){
            binding.fab.visibility = View.GONE
        }

        binding.recyMembers.setHasFixedSize(true)
        binding.recyMembers.layoutManager = LinearLayoutManager(this@MembersActivity)

        adapter = UserListAdapter(this, userList)

        adapter.onAction = object : UserListAdapter.OnAction {
            override fun onDeleteClick(messUser: MessUser) {
                userDeleteCheck(messUser)
            }

        }
        binding.recyMembers.adapter = adapter

        binding.fab.setOnClickListener {
            showAddMemberDialog()
        }

        getUsersList()
        getCountries()
    }

    private fun getCountries() {
        (application as MyApplication)
            .myApi
            .getCountries()
            .enqueue(object : Callback<ServerResponse<MutableList<Country>>> {
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<Country>>>,
                    response: Response<ServerResponse<MutableList<Country>>>
                ) {
                    if(response.isSuccessful && response.body()!=null){
                        val body = response.body()!!
                        if(!body.error){
                            countries.clear()
                            countries.addAll(body.data!!)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<MutableList<Country>>>,
                    t: Throwable
                ) {
                }

            })
    }

    private fun userDeleteCheck(messUser: MessUser) {
//        loadingDialog.show()
//        (application as MyApplication)
//            .myApi
//            .userDeleteCheck(mess, Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
//            .enqueue(object : Callback<GenericRespose> {
//                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
//                    loadingDialog.hide()
//                    if (response.isSuccessful && response.body()!=null){
//                        val body = response.body()!!
//                        if(body.error){
//                            //show warning
//
//                            JDialog.make(this@MembersActivity)
//                                .setCancelable(true)
//                                .setIconType(JDialog.IconType.WARNING)
//                                .setBodyText(body.msg)
//                                .setNegativeButton("Cancel"){
//                                    it.hideDialog()
//                                }.setPositiveButton("Yes! Delete Now"){
//                                    it.hideDialog()
//                                    deleteUser(messUser)
//                                }
//                                .build()
//                                .showDialog()
//
//                        }else{
//                            //account deleted
//
//                            shortToast(body.msg)
//                            getUsersList()
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
//                    loadingDialog.hide()
//                }
//
//            })
    }

    private fun deleteUser(messUser: MessUser) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .userDelete(messUser.id, Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
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
            .enqueue(object: Callback<ServerResponse<List<MessUser>>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<ServerResponse<List<MessUser>>>, response: Response<ServerResponse<List<MessUser>>>) {

                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        val userListResponse = response.body()!!
                        if(!userListResponse.error){
                            val list = userListResponse.data!!
                            setUsers(list)

                        }
                    }
                }
                override fun onFailure(call: Call<ServerResponse<List<MessUser>>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun setUsers(list: List<MessUser>) {
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

        var countryCode : String? = null

        dialogBinding.ccp.setOnCountryChangeListener {
             countryCode = dialogBinding.ccp.selectedCountryCode
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

            var phone = dialogBinding.ccp.fullNumber


            // Get the selected country code
            val code: String = dialogBinding.ccp.getSelectedCountryCode()


            if (phone.startsWith(code)) {
                 phone = phone.substring(code.length)
            }

            Toast.makeText(this, "$phone", Toast.LENGTH_SHORT).show()

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
                .addUser(name, phone, pass, pass, userName, email, city,
                    gender!!.lowercase(Locale.getDefault()), countryCode = countryCode)
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