package com.logicline.mydining.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.facebook.ads.AdSettings
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.network.MyApi
import com.logicline.mydining.ui.FirstActivity
import com.onesignal.OneSignal
import io.paperdb.Paper
import java.util.Locale

class MyApplication : Application() {
    val myApi by lazy {
        MyApi.invoke()
    }
    companion object {
        private val ONESIGNAL_APP_ID =  "583b27bf-ab91-4ece-831e-1513302b7392";
        lateinit  var appContext: Context

        fun isLogged():Boolean{
            if(LocalDB.getAccessToken()!=null && LocalDB.getUserId()!=null && LocalDB.getUser()!=null){
                return true
            }

            return false
        }

        fun logOut(context: Activity){
            LocalDB.logout()
            context.finish()
            context.startActivity(Intent(context, FirstActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }

    }


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Paper.init(this)
        FirebaseApp.initializeApp(applicationContext)
        MobileAds.initialize(this) {}
        // Please make sure to set the mediation provider value to "max" to ensure proper functionality.
        AppLovinSdk.getInstance( applicationContext ).mediationProvider = "max"
        AppLovinSdk.getInstance( applicationContext ).initializeSdk {}

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        newConfig.setLocale(Locale("en"))
        super.onConfigurationChanged(newConfig)
    }


}