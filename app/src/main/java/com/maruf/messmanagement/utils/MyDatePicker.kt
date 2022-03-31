package com.maruf.messmanagement.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import java.time.Year
import java.util.*

class MyDatePicker(val context: Context, val  activity: Activity) {
    var datePickerDialog: DatePickerDialog? = null
    var onDateSelectListener: OnDateSelectListener
    var mDay : Int = 0
    var mMonth : Int = 0
    var mYear : Int = 0

    init {
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        onDateSelectListener = context as OnDateSelectListener
    }

    fun create() : MyDatePicker{

        datePickerDialog?.let {
            return this
        }

         datePickerDialog = DatePickerDialog(context, {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            onDateSelectListener.date(dayOfMonth, monthOfYear, year)
            onDateSelectListener.dateString(Constant.dateFormat(date))

        }, mYear, mMonth, mDay)

        return this
    }

    fun show(){
        datePickerDialog?.show()
    }




    interface OnDateSelectListener{
        fun date(date:Int, month:Int, year: Int)

        fun dateString(date:String)
    }
}