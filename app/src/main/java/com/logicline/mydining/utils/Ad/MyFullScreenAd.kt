package com.logicline.mydining.utils.Ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.utils.MyExtensions.shortToast

class MyFullScreenAd(val context: Context, val isFinishActivity: Boolean = false) {

    private var ADD_UNIT: String
    //private  val ADD_UNIT: String = "ca-app-pub-5848923401370877/7561987130" //real
    init {
        ADD_UNIT = if (BuildConfig.DEBUG){
            "ca-app-pub-3940256099942544/1033173712" //test
        }else{
            "ca-app-pub-5848923401370877/7561987130" //real
        }
    }
    companion object{
        private const val TAG: String = "FullScreenAd"


        private var mInterstitialAd: InterstitialAd? = null



    }
    private lateinit var adRequest:AdRequest


    init {
        loadAd()
    }

    private fun setAdListener() {
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
                finishActivity()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
                finishActivity()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
                AdSettings.setFullScreenAdShownTime()
            }

        }
    }

    private fun finishActivity() {
        if(isFinishActivity){
            (context as Activity).finish()
        }
    }

     fun showAd(){
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(context as Activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
            finishActivity()
        }
    }

    private fun loadAd(){

        if(AdSettings.isFullScreenAdShownAllowed()){

            adRequest = AdRequest.Builder().build()

            Log.d(TAG, "Ad Loading...")

            InterstitialAd.load(context, ADD_UNIT, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let {
                        Log.d(TAG, it)
                    }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    setAdListener()

                }
            })
        }else{
            Log.d(TAG, "Ad Load Not Allowed.")
        }


    }

}