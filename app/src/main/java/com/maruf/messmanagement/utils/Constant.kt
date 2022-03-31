package com.maruf.messmanagement.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.maruf.messmanagement.models.User
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

object Constant {

    val PURCHASE_TYPE: String = "PURCHASE_TYPE"
    val MESS_MANAGER = "2"
    val NORMAL_USER = "0"
    val SUPER_USER = "3 "


    val userKey = "User"

    fun isManager(context: Context):Boolean{
        val user = Paper.book().read<User>(userKey)
        user?.let {
            if(it.accType=="2"){
                return true
            }
        }
        return false
    }
    fun isSuperUser(context: Context):Boolean{
        val user = Paper.book().read<User>(userKey)
        user?.let {
            if(it.accType=="3"){
                return true
            }
        }
        return false
    }

    fun isManagerOrSuperUser():Boolean{
        val user = Paper.book().read<User>(userKey)
        user?.let {
            if(it.accType=="3" || it.accType=="2"){
                return true
            }
        }
        return false
    }


    @SuppressLint("SimpleDateFormat")
    fun getDayNameFromDate(date:String) : String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd")

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("EEEE")
        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormat(date:String) : String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd")

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate() : String{
        val cal = Calendar.getInstance()
        val inFormat = SimpleDateFormat("yyyy-MM-dd")
        return inFormat.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthName(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd")

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("MMMM")
        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    @SuppressLint("SimpleDateFormat")
    fun getMonthNumber(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd")

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("MM")
        val month: Int = simpleDateFormat.format(myDate).toInt()

        return  month.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getYear(date:String):String{
        val inFormat = SimpleDateFormat("yyyy-MM-dd")

        val myDate: Date = inFormat.parse(date)
        val simpleDateFormat = SimpleDateFormat("yyyy")
        val dayName: String = simpleDateFormat.format(myDate)

        return  dayName
    }

    fun getCurrentYear():String{
        return  getYear(getCurrentDate())
    }

    fun getCurrentMonthName():String{
        return  getMonthName(getCurrentDate())
    }

    fun getCurrentMonthNumber():String{
        return getMonthNumber(getCurrentDate())
    }

    fun getCurrentDayName():String{
        return getDayNameFromDate(getCurrentDate())
    }

    fun booleanToInt(value:Boolean):Int{
        return if(value){
            1
        }else{
            0
        }
    }

}