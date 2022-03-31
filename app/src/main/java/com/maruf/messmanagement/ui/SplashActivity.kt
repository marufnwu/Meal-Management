package com.maruf.messmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.maruf.messmanagement.R
import com.maruf.messmanagement.models.response.CheckLoginResponse
import com.maruf.messmanagement.utils.Constant
import com.maruf.messmanagement.utils.MyApplication
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        checkLogin()
    }

    private fun checkLogin() {
        (application as MyApplication).myApi
            .checkLogin()
            .enqueue(object : Callback<CheckLoginResponse> {
                override fun onResponse(
                    call: Call<CheckLoginResponse>, response: Response<CheckLoginResponse>) {
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

                           Toast.makeText(this@SplashActivity, it.msg, Toast.LENGTH_SHORT).show()

                           gotoLoginActivity()
                       }
                   }
                }

                override fun onFailure(call: Call<CheckLoginResponse>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun gotoLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}