package com.logicline.mydining.ui

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.R
import com.logicline.mydining.models.Ad
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.InitialDataResponse
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.JDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirstActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 6666
    lateinit var appUpdate: AppUpdateManager

    private lateinit var referrerClient: InstallReferrerClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkReferrerClient()
        appUpdate = AppUpdateManagerFactory.create(this)
    }

    private fun checkReferrerClient() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.

                        val response: ReferrerDetails = referrerClient.installReferrer
                        val referrerUrl: String = response.installReferrer
                        val referrerClickTime: Long = response.referrerClickTimestampSeconds
                        val appInstallTime: Long = response.installBeginTimestampSeconds
                        val instantExperienceLaunched: Boolean = response.googlePlayInstantParam

                        Log.d("InstallReferrerClient", "onInstallReferrerSetupFinished: "+referrerUrl)
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                        Log.d("InstallReferrerClient", "FEATURE_NOT_SUPPORTED: ")

                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                        Log.d("InstallReferrerClient", "SERVICE_UNAVAILABLE: ")

                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onResume() {
        super.onResume()
        checkUpdate()
    }

    private fun checkUpdate(){
        Log.d("UpdateChecker", "Inside check update")
        appUpdate.appUpdateInfo.addOnSuccessListener { updateInfo ->

            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                Log.d(
                    "UpdateChecker",
                    "Update Available version" + updateInfo.availableVersionCode()
                )

                try {
                    appUpdate.startUpdateFlowForResult(
                        updateInfo,
                        AppUpdateType.IMMEDIATE, this, REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                    getAdSettings()
                }
            } else {
                Log.d("UpdateChecker", "App up to date")
                getAdSettings()
            }

        }.addOnFailureListener {
            Log.d("UpdateChecker", it.message!!)
            getAdSettings()
        }
    }

    private fun getAdSettings(){
        try {
            (application as MyApplication)
                .myApi
                .getAdSettings()
                .enqueue(object: Callback<Ad?> {
                    override fun onResponse(call: Call<Ad?>, response: Response<Ad?>) {
                        try {
                            if(response.isSuccessful && response.body()!=null){
                                LocalDB.saveAdSettings(response.body()!!)
                            }
                        }catch (_:Exception){

                        }

                        checkLogin()


                    }

                    override fun onFailure(call: Call<Ad?>, t: Throwable) {
                        Log.d("RetrofitError", t.message!!)
                        checkLogin()
                    }

                })
        }catch (e:Exception){
            
        }
    }

    private fun checkLogin() {
        if(MyApplication.isLogged()){

            checkAccessToken()

        }else{
            gotoLoginActivity()
        }
    }


    private fun checkAccessToken(){
            try {
                (application as MyApplication).myApi
                    .checkLogin()
                    .enqueue(object : Callback<ServerResponse<User>> {
                        override fun onResponse(
                            call: Call<ServerResponse<User>>,
                            response: Response<ServerResponse<User>>
                        ) {
                            if(response.isSuccessful && response.body()!=null){
                                val body = response.body()!!

                                if(!body.error){
                                    body.data?.let {
                                        LocalDB.saveUser(it)
                                        //gotoMainActivity()
                                        getInitialData()
                                    }
                                }else{
                                    LocalDB.logout()
                                    gotoLoginActivity()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ServerResponse<User>>, t: Throwable) {
                            JDialog.make(this@FirstActivity)
                                .setCancelable(false)
                                .setPositiveButton("Reload"){
                                    it.hideDialog()
                                    checkAccessToken()
                                }
                                .setBodyText("Something went wrong!!")
                                .setIconType(JDialog.IconType.ERROR)
                                .build()
                                .showDialog()
                        }

                    })


            }catch (e:Exception){

            }

    }

    private fun getInitialData() {
        (application as MyApplication)
            .myApi
            .getInitialData(BuildConfig.VERSION_CODE)
            .enqueue(object : Callback<InitialDataResponse> {
                override fun onResponse(
                    call: Call<InitialDataResponse>,
                    response: Response<InitialDataResponse>) {
                    if(response.isSuccessful){
                        response.body()?.let { initialDataResponse ->
                            if(!initialDataResponse.error){
                                initialDataResponse.initialData?.let {
                                    LocalDB.saveInitialData(it)
                                    gotoMainActivity()
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<InitialDataResponse>, t: Throwable) {
                    com.maruf.jdialog.JDialog.make(this@FirstActivity)
                        .setCancelable(false)
                        .setBodyText("Something went wrong! Please try again.")
                        .setIconType(com.maruf.jdialog.JDialog.IconType.ERROR)
                        .setPositiveButton("Try Again"){
                            it.hideDialog()
                            getInitialData()
                        }.build()
                        .showDialog()
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

    private fun reopen(){

        val intent = baseContext.packageManager.getLaunchIntentForPackage(
            baseContext.packageName
        )
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //  handle user's approval }
                    shortToast("App Successfully updated")
                    getAdSettings()
                }
                Activity.RESULT_CANCELED -> {
                    //  handle user's rejection  }
                    checkUpdate()
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    shortToast("App update failed")
                    checkUpdate()
                }
            }
        }
    }
}