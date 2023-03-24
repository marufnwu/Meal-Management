package com.logicline.mydining.utils.Ad

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.logicline.mydining.R
import com.logicline.mydining.utils.Constant
import java.util.*

class MyRangeDatePicker(
      val context: Context,
      val onDateSelectListener: OnDateSelectListener,
      var mDay : Int? = null,
      var mMonth : Int? = null,
      var mYear : Int? = null, ) {
      var datePickerDialog: DatePickerDialog ? = null

    init {
        val c: Calendar = Calendar.getInstance()
        Constant.getCurrentMonthNumber()

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

    fun create() : MyRangeDatePicker{

        datePickerDialog?.let {
            return this
        }
         datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialog, {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            onDateSelectListener.date(dayOfMonth, monthOfYear, year)
            onDateSelectListener.dateString(Constant.dateFormat(date))

        }, mYear!!, mMonth!!, mDay!!)

        datePickerDialog?.datePicker?.maxDate = System.currentTimeMillis()
        datePickerDialog?.datePicker?.minDate = System.currentTimeMillis()
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