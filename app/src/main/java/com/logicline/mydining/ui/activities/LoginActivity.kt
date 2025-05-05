package com.logicline.mydining.ui.activities

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.logicline.mydining.data.models.Support
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.databinding.ActivityLoginBinding
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

private const val TAG = "LoginActivity"
class LoginActivity : BaseActivity() {

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
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.txtPrivacyPolicy.setOnClickListener {
            Constant.openPrivacyPolicy(this)
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

//        binding.txtLang.setText(LangUtils.getFullLanguage(this, LangUtils.getLanguage(this)))
//        binding.txtLang.setOnClickListener {
//            LanguageSelectorDialog.Builder(this)
//                .build()
//                .show()
//        }
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
            .enqueue(object : Callback<ServerResponse<UserData>> {
                override fun onResponse(call: Call<ServerResponse<UserData>>, response: Response<ServerResponse<UserData>>) {

                    loadingDialog.hide()
                    shortToast( response.body()?.msg)

                    if(response.isSuccessful && response.body()!=null){
                        val loginResponse = response.body()

                        shortToast(loginResponse?.msg)

                        loginResponse?.let {
                            Log.d(TAG, "onResponse: not error")

                            if(!it.error){
                                Log.d(TAG, "onResponse: not error")
                                it.data?.let {
                                    Log.d(TAG, "onResponse: data not null")

                                    if(it.token?.isNotEmpty() == true  && it.user!=null){
                                        LocalDB.saveUserData(it)
                                        LocalDB.saveUser(it.user!!)
                                        LocalDB.saveAccessToken(it.token!!)
                                        LocalDB.saveUserId(it.user?.id!!)
                                        gotoMainActivity()
                                        return
                                    }
                                }




                            }

                            Toast.makeText(this@LoginActivity, it.msg, Toast.LENGTH_SHORT).show()

                        }?: shortToast("aassasas")
                    }else{
                        shortToast(response.message())
                    }
                }

                override fun onFailure(call: Call<ServerResponse<UserData>>, t: Throwable) {
                    if(t is UnknownHostException){
                        shortToast("Your Internet Connection Not Working. Please try again")
                    }else if (t is NetworkErrorException){
                        shortToast("Something went wrong. Contact with support center")
                    }else{
                        shortToast(t.message)
                    }
                    t.printStackTrace()
                    loadingDialog.hide()

                }

            })
    }

    private fun gotoMainActivity() {
        val intent = Intent(
            this,
            MainActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}