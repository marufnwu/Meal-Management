package com.logicline.mydining.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import com.google.android.material.datepicker.MaterialDatePicker
import com.logicline.mydining.R
import com.logicline.mydining.utils.MyExtensions.shortToast
import java.time.Year
import java.util.*

private const val TAG = "MyDatePicker"
class MyDatePicker(
        val context: Context,
        val onDateSelectListener: OnDateSelectListener?,
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

    }

    fun create() : MyDatePicker{

         datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialog, {
                view, year, monthOfYear, dayOfMonth ->

            val month = monthOfYear+1

            val date = "$year-$month-$dayOfMonth"

            onDateSelectListener?.date(dayOfMonth, month, year)
            onDateSelectListener?.dateString(Constant.dateFormat(date))

        }, mYear!!, mMonth!!-1, mDay!!)

        return this
    }

    fun show(){
        datePickerDialog?.show()
    }




    interface OnDateSelectListener{
        fun date(date:Int?, month:Int?, year: Int?)
        fun month(monthId : Int) {

        }
        fun dateString(date:String)
    }
}