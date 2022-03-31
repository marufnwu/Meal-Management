package com.maruf.messmanagement.utils

import android.app.Application
import android.content.Context
import com.maruf.messmanagement.network.MyApi
import io.paperdb.Paper

class MyApplication : Application() {
    val myApi by lazy {
        MyApi.invoke()
    }
    companion object {

        lateinit  var appContext: Context

    }


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Paper.init(this)
    }


}