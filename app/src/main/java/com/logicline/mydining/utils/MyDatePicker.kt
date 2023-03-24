package com.logicline.mydining.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import com.google.android.material.datepicker.MaterialDatePicker
import com.logicline.mydining.R
import java.time.Year
import java.util.*

class MyDatePicker(
        val context: Context,
        val onDateSelectListener: OnDateSelectListener,
       var mDay : Int? = null,
       var mMonth : Int? = null,
       var mYear : Int? = null, ) {
    var datePickerDialog: DatePickerDialog? = null

    init {

        if(mDay==null){
            mDay = Constant.getDay(Constant.getCurrentDate()).toInt()
        }

        if(mYear==null){
            mYear = Constant.getCurrentYear().toInt()

        }

        if(mMonth==null){
            mMonth = Constant.getCurrentMonthNumber().toInt()
        }

        mMonth = mMonth!! -1




    }

    fun create() : MyDatePicker{

        datePickerDialog?.let {
            return this
        }

        val c: Calendar = Calendar.getInstance(Locale.ENGLISH)
        c.set(Calendar.DATE, mDay!!)
        c.set(Calendar.MONTH, mMonth!!)
        c.set(Calendar.YEAR, mYear!!)

         datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialog, {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            onDateSelectListener.date(dayOfMonth, monthOfYear, year)
            onDateSelectListener.dateString(Constant.dateFormat(date))

        }, mYear!!, mMonth!!, mDay!!)

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