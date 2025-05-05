package com.logicline.mydining.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityCreateMessBinding
import com.logicline.mydining.data.models.response.CheckLoginResponse
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.MyApplication
import com.logicline.mydining.data.models.User
import com.logicline.mydining.utils.MyExtensions.shortToast
import com.maruf.jdialog.JDialog

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : BaseActivity() {
    lateinit var loadingDialog: LoadingDialog
    private var userNameChecking = false
    lateinit var binding : ActivityCreateMessBinding
    var gender : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private  fun initViews() {
        loadingDialog = LoadingDialog(this)


        binding.ccp.setAutoDetectedCountry(true)
        binding.ccp.setNumberAutoFormattingEnabled(true)
        binding.ccp.setHintExampleNumberEnabled(true)
        binding.ccp.registerCarrierNumberEditText(binding.edtPhone)


        ArrayAdapter.createFromResource(this, R.array.gender, R.layout.layout_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerGender.adapter = adapter
        }


        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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



        binding.btnSubmit.setOnClickListener {
            createMess()
        }




    }


    private fun createMess(){
        val name = binding.edtName.text.toString()
        val pass = binding.edtPass.text.toString()
        //val phone = binding.edtPhone.text.toString()
        val userName = binding.edtUerName.text.toString()
        val email = binding.edtEmail.text.toString()
        val city = binding.edtCity.text.toString()

        val country = binding.ccp.selectedCountryNameCode




        if(country==null){
            shortToast("Please select country first.")
            return
        }


        if(!binding.ccp.isValidFullNumber){
            shortToast("Please enter valid mobile number")
            return
        }

        var phone = binding.ccp.fullNumber
        val code: String = binding.ccp.selectedCountryCode

        if (phone.startsWith(code)) {
            phone = phone.substring(code.length)
        }

        if(pass.length<6){
            Toast.makeText(this, "Password must not less than 6 character", Toast.LENGTH_SHORT).show()
            return
        }

        if(gender==null){
            shortToast("Please select your gender")
            return
        }

        if(email.isNullOrEmpty()){
            shortToast("Email must not empty")
            return
        }

        if(!Constant.isValidEmail(email)){
            shortToast("Email is not valid")
            return
        }

        if(name.isEmpty() || pass.isEmpty() || city.isEmpty()|| userName.isEmpty()){
            Toast.makeText(this, "Incorrect Data", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show()
        (application as MyApplication).myApi
            .signUp( name, userName, code,
                phone, pass, pass, email, city, gender!!)
            .enqueue(object: Callback<ServerResponse<User>> {
                override fun onResponse(
                    call: Call<ServerResponse<User>>, response: Response<ServerResponse<User>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){

                        if(!response.body()!!.error){
                            val loginResponse = response.body()

                            loginResponse?.let {
                                if(!it.error){


                                    shortToast("Account Created Successfully. Please login to continue.")

                                    startActivity(
                                        Intent(this@SignUpActivity, LoginActivity::class.java)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    )

                                    finish()


                                }

                            }

                        }else{
                            JDialog.make(this@SignUpActivity)
                                .setBodyText(response.body()!!.errors?.joinToString("\n") { it.toString() })
                                .setIconType(JDialog.IconType.ERROR)
                                .setCancelable(true)
                                .setPositiveButton("Okay"){
                                    it.hideDialog()
                                }
                                .build()
                                .showDialog()

                        }

                    }
                }

                override fun onFailure(call: Call<ServerResponse<User>>, t: Throwable) {
                    loadingDialog.hide()

                }

            })
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return false
    }


}