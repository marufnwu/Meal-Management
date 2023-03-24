package com.logicline.mydining.utils.Ad

import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class ApplovinNativeAd(val context: Context, val listener: Listener) {

    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null

    interface Listener{
        fun onAdLoaded()
        fun forNativeAd(ad : MaxAd, adView: MaxNativeAdView)
        fun onError()
    }

    init {
        createNativeAd()
    }

    private fun createNativeAd() {
        nativeAdLoader = MaxNativeAdLoader("8235e006d6e2fdf7", context)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {

            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd)
            {
                Log.d("ApplovinNativeAd", "Loaded")
                // Clean up any pre-existing native ad to prevent memory leaks.
                if ( nativeAd != null )
                {
                    nativeAdLoader.destroy( nativeAd )
                }

                // Save ad for cleanup.
                nativeAd = ad

                listener.forNativeAd(ad, nativeAdView!!)

            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError)
            {
                // We recommend retrying with exponentially higher delays up to a maximum delay
                Log.d("ApplovinNativeAd", error.code.toString()+" "+error.message)

            }

            override fun onNativeAdClicked(ad: MaxAd)
            {
                // Optional click callback
            }
        })
        Log.d("ApplovinNativeAd", "Loading")

        nativeAdLoader.loadAd()
    }

}