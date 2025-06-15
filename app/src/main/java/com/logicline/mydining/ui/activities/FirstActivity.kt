package com.logicline.mydining.ui.activities

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.logicline.mydining.R
import com.logicline.mydining.data.models.Ad
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.ui.activities.MainActivity
import com.logicline.mydining.utils.JDialog
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.MyApplication
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.Ext.MyExtensions.handle
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class FirstActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 6666
    lateinit var appUpdate: AppUpdateManager

    private lateinit var referrerClient: InstallReferrerClient
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkReferrerClient()
        appUpdate = AppUpdateManagerFactory.create(this)



        listenFLow()
    }

    private fun listenFLow() {
        lifecycleScope.launchWhenStarted {
            userViewModel.loginState.collect { loginState ->
                loginState.handle(
                    onError = {
                        MyApplication.logOut(this@FirstActivity)
                        gotoLoginActivity()
                    },
                    onSuccess = {
                        processData(it)

                    }
                )
            }
        }


    }

    private fun processData(userData: UserData?) {
        lifecycleScope.launch {
            userViewModel.saveUserDataLocally(userData)

            if (userData?.messUser != null) {
                gotoMainActivity()
            }else{
                gotoMessActivity()
            }
        }

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

                        Log.d(
                            "InstallReferrerClient",
                            "onInstallReferrerSetupFinished: " + referrerUrl
                        )
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
        startActivity(Intent(this@FirstActivity, DemoActivity::class.java))
        finish()
//        checkUpdate()
    }

    private fun checkUpdate() {
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

    private fun getAdSettings() {
        Log.d("getAdSettings", "getAdSettings: calling")
        try {
            (application as MyApplication)
                .myApi
                .getAdSettings()
                .enqueue(object : Callback<Ad?> {
                    override fun onResponse(call: Call<Ad?>, response: Response<Ad?>) {
                        try {
                            if (response.isSuccessful && response.body() != null) {
                                LocalDB.saveAdSettings(response.body()!!)
                            }
                        } catch (_: Exception) {

                        }

                        checkLogin()


                    }

                    override fun onFailure(call: Call<Ad?>, t: Throwable) {
                        Log.d("RetrofitError", t.message!!)
                        checkLogin()
                    }

                })
        } catch (e: Exception) {
            checkLogin()
        }
    }

    private fun checkLogin() {
        Log.d("checkLogin: ", MyApplication.isLogged().toString())
        if (MyApplication.isLogged()) {
            checkAccessToken()
        } else {
            gotoLoginActivity()
        }
    }


    private fun checkAccessToken() {

        userViewModel.checkLogin()
    }


    private fun gotoLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun gotoMainActivity() {
//        val intent = Intent(this, MainActivity::class.java)
        val intent = Intent(this, DemoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun gotoMessActivity() {
        val intent = Intent(this, MessInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun reopen() {

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
                RESULT_OK -> {
                    //  handle user's approval }
                    shortToast("App Successfully updated")
                    getAdSettings()
                }

                RESULT_CANCELED -> {
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