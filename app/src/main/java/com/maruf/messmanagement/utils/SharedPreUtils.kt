package com.maruf.messmanagement.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.maruf.messmanagement.models.User
import java.util.*

object SharedPreUtils {
    val userKey  = "user"


    var sharedPreferences: SharedPreferences?=null
    val sharedPreferenceName: String= "Meal Management"

    private fun initSharedPref(context: Context): SharedPreferences {
        if (sharedPreferences==null) {
            sharedPreferences=context.getSharedPreferences(sharedPreferenceName,MODE_PRIVATE)
        }
        return sharedPreferences!!
    }


    suspend fun setStringToStorage(context: Context,key: String, value: String) {
        val editor: SharedPreferences.Editor=initSharedPref(context).edit()
        editor.putString(key,value)
        editor.apply()
    }

    suspend fun setBooleanToStorage(context: Context,key: String, value: Boolean) {
        val editor: SharedPreferences.Editor=initSharedPref(context).edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    suspend fun setIntToStorage(context: Context,key: String, value: Int) {
        val editor: SharedPreferences.Editor=initSharedPref(context).edit()
        editor.putInt(key,value)
        editor.apply()
    }

    suspend fun setLongToStorage(context: Context,key: String, value: Long) {
        val editor: SharedPreferences.Editor=initSharedPref(context).edit()
        editor.putLong(key,value)
        editor.apply()
    }

    suspend fun getLongFromStorage(context: Context,key: String, defaultValue: Long): Long {
        return initSharedPref(context).getLong(key,defaultValue)
    }

    suspend fun getStringFromStorage(context: Context,key: String, defaultValue: String?) : String? {
        return initSharedPref(context).getString(key,defaultValue)
    }

    fun getStringFromStorageWithoutSuspend(context: Context,key: String, defaultValue: String?) : String? {
        return initSharedPref(context).getString(key,defaultValue)
    }

    suspend fun getBooleanFromStorage(context: Context,key: String, defaultValue: Boolean) : Boolean {
        return initSharedPref(context).getBoolean(key,defaultValue)
    }

    suspend fun getIntFromStorage(context: Context,key: String, defaultValue: Int) : Int {
        return initSharedPref(context).getInt(key,defaultValue)
    }



}