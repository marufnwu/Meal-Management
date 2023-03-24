package com.logicline.mydining.utils.Ad

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.logicline.mydining.R

class AdmobNativeAd(val context: Context, val listener: Listener)  {
    companion object{
        //private val adId = "ca-app-pub-3940256099942544/2247696110" //test
        private val adId = "ca-app-pub-5848923401370877/7980504125"; //real
    }
    private var nativeAd : NativeAd? = null
    interface Listener{
        fun onAdLoaded()
        fun forNativeAd(ad :NativeAd, adView:NativeAdView)
        fun onError()
    }
    init {
        //MobileAds.initialize(context) {}
        loadAd()
    }

    private fun loadAd(){
        Log.d("AdmobNativeAd", "Ad loading called")

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()


        val builder = AdLoader.Builder(context, adId)
            .withNativeAdOptions(adOptions)
            .forNativeAd { ad ->
                // Assumes that your ad layout is in a file call native_ad_layout.xml
                Log.d("AdmobNativeAd","Loaded")
                nativeAd = ad
                nativeAd?.let {
                    AdSettings.setNativeAdShownTime()
                    displayNativeAd(it)
                }
            }

            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d("AdmobNativeAd", "Error "+p0.message)
                    listener.onError()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    listener.onAdLoaded()
                }
            }).build()

        builder.loadAd(AdRequest.Builder().build())
    }

    fun displayNativeAd(nativeAd: NativeAd) {

        val adView =  (LayoutInflater.from(context).inflate(R.layout.ad_unified, null)) as NativeAdView

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        //adView.priceView = adView.findViewById(com.logic line.mydining.R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        //adView.storeView = adView.findViewById(com.logic line.mydining.R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)


        (adView.headlineView as TextView).text = nativeAd.headline


        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }


        if (nativeAd.callToAction == null) {

            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

//        if (nativeAd.price == null) {
//            adView.priceView?.visibility = View.INVISIBLE
//        } else {
//            adView.priceView?.visibility = View.VISIBLE
//            (adView.priceView as TextView).text = nativeAd.price
//        }
//
//        if (nativeAd.store == null) {
//            adView.storeView?.visibility = View.INVISIBLE
//        } else {
//            adView.storeView?.visibility = View.VISIBLE
//            (adView.storeView as TextView).text = nativeAd.store
//        }

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Ensure that the parent view doesn't already contain an ad view.

        listener.forNativeAd(nativeAd, adView)

    }

}

