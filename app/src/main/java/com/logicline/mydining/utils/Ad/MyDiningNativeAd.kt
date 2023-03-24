package com.logicline.mydining.utils.Ad

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.logicline.mydining.R
import com.logicline.mydining.utils.MyExtensions.lifecycleOwner

class MyDiningNativeAd(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {


    lateinit var adContainer: FrameLayout
    private var nativeAd : NativeAd? = null
    var listener : Listener?  =null
    private var showLoading = true
    private var admobNativeAd: AdmobNativeAd? = null
    private var applovinNativeAd: ApplovinNativeAd? = null

    interface Listener{
        fun onAdLoaded()
        fun forNativeAd(ad : NativeAd)
        fun onError()
    }

    init {
        Log.d("CustomNativeAddCalled", "Called")

        initViews()

        context.theme.obtainStyledAttributes(
            attrs,
            com.logicline.mydining.R.styleable.AdmobCustomNativeAdView,
            0, 0).apply {
            try {
                showLoading = getBoolean(R.styleable.AdmobCustomNativeAdView_showLoading, false)

            } finally {
                recycle()
            }
        }


    }

    private fun initViews(){
        inflate(context, R.layout.custom_admob_native_ad_layout, this)
        this.visibility = View.GONE
        adContainer = findViewById(R.id.ad_container)

        setLifeCycle()

        if(AdSettings.isNativeAdShownAllowed()){


            if(AdSettings.getNativeAdType()==AdSettings.PLATFORM.ADMOB){
                admobNativeAd = AdmobNativeAd(context, object : AdmobNativeAd.Listener {
                    override fun onAdLoaded() {

                    }

                    override fun forNativeAd(ad: NativeAd, adView: NativeAdView) {
                        Log.d("AdmobNativeAd","Show Called")

                        adContainer.removeAllViews()
                        adContainer.addView(adView)
                        this@MyDiningNativeAd.visibility = View.VISIBLE
                    }

                    override fun onError() {

                    }

                })
            }else if(AdSettings.getNativeAdType()== AdSettings.PLATFORM.APPLOVIN){
                applovinNativeAd = ApplovinNativeAd(context, object: ApplovinNativeAd.Listener {
                    override fun onAdLoaded() {

                    }

                    override fun forNativeAd(ad: MaxAd, adView: MaxNativeAdView) {
                        adContainer.removeAllViews()
                        adContainer.addView(adView)
                        this@MyDiningNativeAd.visibility = View.VISIBLE
                    }

                    override fun onError() {
                        this@MyDiningNativeAd.visibility = View.GONE

                    }

                })
            }





        }else{
            listener?.onError()
            nativeAd?.destroy()
        }




    }

    private fun setLifeCycle() {
        val lifeCycle = context.lifecycleOwner()
        lifeCycle?.lifecycle?.addObserver(LifecycleEventObserver { source, event ->
            if (Lifecycle.Event.ON_DESTROY == event) {
                nativeAd?.destroy()
            }
        })

    }

}