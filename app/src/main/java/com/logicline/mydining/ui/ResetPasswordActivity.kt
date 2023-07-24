package com.logicline.mydining.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityResetPasswordBinding
import com.logicline.mydining.models.OtpRequest
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.change_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)

        searchAccount()

    }

    private fun searchAccount() {
        binding.cardOtp.visibility = View.GONE
        binding.cardPassword.visibility = View.GONE
        binding.cardSearch.visibility = View.VISIBLE

        binding.btnSearch.setOnClickListener {

            val username = binding.edtUserName.text.toString()
            if(username.isEmpty()){
                shortToast("Username must not empty")
                return@setOnClickListener
            }

            search(username)
        }
    }

    private fun search(username: String) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .otpRequest(username)
            .enqueue(object : Callback<ServerResponse<OtpRequest>> {
                override fun onResponse(
                    call: Call<ServerResponse<OtpRequest>>, response: Response<ServerResponse<OtpRequest>>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            showOtpInput(response.body()!!.data)
                        }else{
                            shortToast(response.body()!!.msg)
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<OtpRequest>>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })


    }

    private fun showOtpInput(otpRequest: OtpRequest?) {
        if(otpRequest!=null){
            binding.cardOtp.visibility  =View.VISIBLE
            binding.cardSearch.visibility = View.GONE
            binding.cardPassword.visibility  =View.GONE

            binding.txtEmail.text = "Otp send to your ${otpRequest.email}. Enter your otp which is send to your email address."
            binding.txtEmail.visibility = View.VISIBLE


            binding.btnVerify.setOnClickListener {
                val userOtp = binding.edtOtp.text.toString()
                if(userOtp.length<4){
                    shortToast("OTP must be 4 character length")
                    return@setOnClickListener
                }

                verifyOtp(otpRequest, userOtp)
            }




        }
    }

    private fun verifyOtp(otpRequest: OtpRequest, userOtp: String) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .veryfyOtp(userOtp,otpRequest.otpId)
            .enqueue(object: Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(response.body()!!.error){
                            shortToast(response.body()!!.msg)
                        }else{
                            showChangePassword(otpRequest)
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }

    private fun showChangePassword(otpRequest: OtpRequest) {
        binding.cardOtp.visibility  =View.GONE
        binding.cardSearch.visibility = View.GONE
        binding.cardPassword.visibility  =View.VISIBLE

        binding.btnChangePassword.setOnClickListener {
            val pass1 = binding.edtPassword1.text.toString()
            val pass2 = binding.edtPassword2.text.toString()

            if(pass1.length<6){
               shortToast("Password must be grater than 5 character length")
               return@setOnClickListener
            }

            if(pass1!=pass2){
                shortToast("Password not matched")
                binding.edtPassword2.error = "Enter same password"
                return@setOnClickListener
            }

            changePassword(otpRequest, pass1)
        }



    }

    private fun changePassword(otpRequest: OtpRequest, pass1: String) {
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .resetPassword(otpRequest.otpId, otpRequest.userId, pass1)
            .enqueue(object: Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        shortToast(response.body()!!.msg)

                        if(!response.body()!!.error){
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                    shortToast(t.message)
                }

            })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}