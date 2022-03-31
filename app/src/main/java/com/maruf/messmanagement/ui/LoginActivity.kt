package com.maruf.messmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.maruf.messmanagement.R
import com.maruf.messmanagement.databinding.ActivityLoginBinding
import com.maruf.messmanagement.models.response.CheckLoginResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.MyApplication
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            login();
        }
    }

    private fun login() {
        val phone = binding.edtPhone.text.toString()
        val password = binding.edtPassword.text.toString()

        if(phone.isNullOrEmpty() ||  password.isNullOrEmpty()){
            Toast.makeText(this, "No filed must not blank", Toast.LENGTH_SHORT).show()
            return
        }

        (application as MyApplication)
            .myApi
            .login(phone, password)
            .enqueue(object : Callback<CheckLoginResponse> {
                override fun onResponse(
                    call: Call<CheckLoginResponse>,
                    response: Response<CheckLoginResponse>
                ) {
                    if(response.isSuccessful && response.body()!=null){
                        val loginResponse = response.body()
                        loginResponse?.let {
                            if(!it.error){
                                it.user?.let {
                                        user ->  Paper.book().write(Constant.userKey, user)
                                    gotoMainActivity()
                                    return
                                }
                            }

                            Toast.makeText(this@LoginActivity, it.msg, Toast.LENGTH_SHORT).show()

                        }
                    }

                }

                override fun onFailure(call: Call<CheckLoginResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}