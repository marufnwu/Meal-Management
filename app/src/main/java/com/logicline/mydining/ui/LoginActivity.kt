package com.logicline.mydining.ui

import android.accounts.NetworkErrorException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.logicline.mydining.databinding.ActivityLoginBinding
import com.logicline.mydining.models.Support
import com.logicline.mydining.models.response.CheckLoginResponse
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.Coroutines
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        getSupport()

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnNewMess.setOnClickListener {
            startActivity(Intent(this, CreateMessActivity::class.java))
        }

        binding.txtPrivacyPolicy.setOnClickListener {
            Constant.openPrivacyPolicy(this)
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    private fun getSupport() {
                   (application as MyApplication)
                        .myApi
                        .getSupport()
                        .enqueue(object : Callback<Support> {
                            override fun onResponse(
                                call: Call<Support>,
                                res: Response<Support>
                            ) {
                                if(res.isSuccessful && res.body()!=null){
                                    res.body()?.let {support->
                                        binding.cardContactUs.visibility = View.VISIBLE

                                        binding.cardContactUs.setOnClickListener {
                                            if(support.type=="whatsapp"){
                                                Constant.openWpCustomerCare(this@LoginActivity, support.action)
                                            }else if(support.type=="link"){
                                                Constant.openLink(this@LoginActivity, support.action)
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Support>, t: Throwable) {

                            }

                        })

    }

    private fun login() {
        val userName = binding.edtUerName.text.toString()
        val password = binding.edtPassword.text.toString()

        if(userName.isEmpty() ||  password.isEmpty()){
            Toast.makeText(this, "Every field are required", Toast.LENGTH_SHORT).show()
            return
        }
        loadingDialog.show()
        (application as MyApplication)
            .myApi
            .login(userName, password)
            .enqueue(object : Callback<ServerResponse<CheckLoginResponse>> {
                override fun onResponse(call: Call<ServerResponse<CheckLoginResponse>>, response: Response<ServerResponse<CheckLoginResponse>>) {

                    loadingDialog.hide()

                    if(response.isSuccessful && response.body()!=null){
                        val loginResponse = response.body()

                        loginResponse?.let {
                            if(!it.error){

                                it.data?.let {
                                    if(it.token.isNotEmpty() && it.userId>0 && it.user!=null){

                                        LocalDB.saveUser(it.user!!)
                                        LocalDB.saveAccessToken(it.token)
                                        LocalDB.saveUserId(it.userId)
                                        gotoMainActivity()

                                        return
                                    }
                                }




                            }

                            Toast.makeText(this@LoginActivity, it.msg, Toast.LENGTH_SHORT).show()

                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<CheckLoginResponse>>, t: Throwable) {
                    if(t is UnknownHostException){
                        shortToast("Your Internet Connection Not Working. Please try again")
                    }else if (t is NetworkErrorException){
                        shortToast("Something went wrong. Contact with support center")
                    }
                    loadingDialog.hide()

                }

            })
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}