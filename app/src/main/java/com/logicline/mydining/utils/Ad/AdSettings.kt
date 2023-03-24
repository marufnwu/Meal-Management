package com.logicline.mydining.utils.Ad

import android.util.Log
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LocalDB
import io.paperdb.Paper
import java.util.concurrent.TimeUnit

object AdSettings {

    enum class PLATFORM{
        ADMOB,
        APPLOVIN,
        UNDEFINED
    }

    private const val KEY_LAST_FULLSCREEN_AD_TIME = "FullScreenAdTime"
    private const val KEY_LAST_NATIVE_AD_TIME = "NativeAdTime"

    fun getNativeAdType() : PLATFORM{
        val ad = LocalDB.getAdSettings()

        ad?.native?.platform?.let {it->
            val platForm = it.toUpperCase()
            return when (platForm) {
                PLATFORM.ADMOB.name -> {
                    PLATFORM.ADMOB
                }
                PLATFORM.APPLOVIN.name -> {
                    PLATFORM.APPLOVIN
                }
                else -> {
                    PLATFORM.UNDEFINED
                }
            }
        }

        return PLATFORM.UNDEFINED

    }

    fun setFullScreenAdShownTime() {
        val currentTimesInMill = System.currentTimeMillis()
        Paper.book().write(KEY_LAST_FULLSCREEN_AD_TIME, currentTimesInMill)
    }
    fun setNativeAdShownTime() {
        val currentTimesInMill = System.currentTimeMillis()
        Paper.book().write(KEY_LAST_NATIVE_AD_TIME, currentTimesInMill)
    }

    fun getLastFullScreenAdShownTime(): Long {
        return Paper.book().read<Long?>(KEY_LAST_FULLSCREEN_AD_TIME, 0L)!!
    }
    fun getLastNativeAdShownTime(): Long {

        val t=  Paper.book().read(KEY_LAST_NATIVE_AD_TIME, 0L)
        return t!!
    }

    fun lastFullScreenAdShownAfter(): Long {
        Log.d("FullScreenAd", "Last fullscreen ad shown ${getLastFullScreenAdShownTime()} At")
        Log.d("FullScreenAd", "Current ${System.currentTimeMillis()} At")

        val difference = System.currentTimeMillis() - getLastFullScreenAdShownTime()
        val d = TimeUnit.MILLISECONDS.toMinutes(difference)
        Log.d("FullScreenAd", "Last fullscreen ad shown $d min ago")

        return d
    }

    fun lastNativeAdShownAfter(): Long {
        val difference = System.currentTimeMillis() - getLastNativeAdShownTime()
        val d = TimeUnit.MILLISECONDS.toMinutes(difference)
        return d
    }

    fun isFullScreenAdShownAllowed():Boolean{
        val ad = LocalDB.getAdSettings()
        val user = LocalDB.getUser()
        if(user!==null){
            if(user.adFree==0){
                if(ad!=null){
                    if(ad.show){
                        if(ad.fullScreen.show){
                            if(Constant.getAccAgeInDays()>=ad.fullScreen.accAge){
                                if(lastFullScreenAdShownAfter()>= ad.fullScreen.interval){
                                    return true
                                }else{
                                    Log.d("FullScreenAd", "FullscreenInterval not expired")
                                }

                            }else{
                                Log.d("FullScreenAd", "Account age is less than allowed age")
                            }
                        }else{
                            Log.d("FullScreenAd", "Full Screen ad show off")
                        }
                    }else{
                        Log.d("FullScreenAd", "Ad show Off")
                    }
                }else{
                    Log.d("FullScreenAd", "Ad settings null")
                }
            }else{
                Log.d("FullScreenAd", "Mess is ad feee")

            }

        }else{
            Log.d("FullScreenAd", "User null")
        }


        return false
    }

    fun isNativeAdShownAllowed():Boolean{
        val ad = LocalDB.getAdSettings()
        val user = LocalDB.getUser()
        if(user!==null){
            if(user.adFree==0){
                if(ad!=null){
                    if(ad.show){
                        if(ad.native.show){
                            Log.d("FullScreenAd", "Acc age "+Constant.getAccAgeInDays())


                            if(Constant.getAccAgeInDays()>=ad.native.accAge){
                                if(lastNativeAdShownAfter()>= ad.native.interval){
                                    return true
                                }else{
                                    Log.d("FullScreenAd", "Interval not expired")
                                }

                            }else{
                                Log.d("FullScreenAd", "Account age is less than allowed age")
                            }
                        }else{
                            Log.d("FullScreenAd", "Full Screen ad show off")
                        }
                    }else{
                        Log.d("FullScreenAd", "Ad show Off")
                    }
                }else{
                    Log.d("FullScreenAd", "Ad settings null")
                }
            }else{
                Log.d("FullScreenAd", "Mess is ad feee")

            }

        }else{
            Log.d("FullScreenAd", "User null")
        }


        return false
    }
}