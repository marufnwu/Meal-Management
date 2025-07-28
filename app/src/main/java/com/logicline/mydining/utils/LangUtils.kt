package com.logicline.mydining.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.google.firebase.crashlytics.ktx.R
import java.util.Locale

object LangUtils {

    fun changeLanguage(context: Context, lanCode:Constant.LANGUAGE){
        SharedPreUtils.setStringToStorage(context, Constant.APP_LANG_KEY, lanCode.name)
    }

    fun getLanguage(context: Context):String{
        return SharedPreUtils.getStringFromStorageWithoutSuspend(context, Constant.APP_LANG_KEY, Constant.LANGUAGE.en_US.name)!!
    }

    fun getFullLanguage(context: Context, langCode : String) : String{
        return when(langCode){
            Constant.LANGUAGE.en_US.name -> context.getString(com.logicline.mydining.R.string.english_lang)
            Constant.LANGUAGE.bn.name -> context.getString(com.logicline.mydining.R.string.bangla_lang)
            else -> {""}
        }
    }

    fun applyLanguage(context: Context): Context {

        val local: Locale = Locale(getLanguage(context))
        val res: Resources =context.resources
        val config: Configuration =res.configuration

        Locale.setDefault(local)
        config.setLocale(local)
        config.setLayoutDirection(local)
        return context.createConfigurationContext(config)
    }
}