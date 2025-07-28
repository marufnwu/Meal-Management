package com.logicline.mydining.ui.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.logicline.mydining.R
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast

class MonthPickerView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    public var onDateSelectListener: MyDatePicker.OnDateSelectListener? = null
    private lateinit var view: View
    private lateinit var txtDate: TextView
    private lateinit var layPrevious: RelativeLayout
    private lateinit var layNext: RelativeLayout

    private lateinit var datePicker : MyDatePicker

    var mDay: Int = Constant.getDay(Constant.getCurrentDate()).toInt()
    var mMonth: Int = Constant.getMonthNumber(Constant.getCurrentDate()).toInt()
    var mYear: Int = Constant.getYear(Constant.getCurrentDate()).toInt()

    init {
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        view = inflate(context, R.layout.layout_date_picker_view, this)
        txtDate = view.findViewById(R.id.txtDate)
        layPrevious = view.findViewById(R.id.layPrevious)
        layNext = view.findViewById(R.id.layNext)

        datePicker = MyDatePicker(context, object : MyDatePicker.OnDateSelectListener {
            override fun date(date: Int, month: Int, year: Int) {
                mDay = date
                mMonth = month
                mYear = year
                updateDateTextView()
                onDateSelectListener?.date(date, month, year)
            }

            override fun dateString(date: String) {
                onDateSelectListener?.dateString(date)
            }

        }, mDay, mMonth, mYear)



        layPrevious.setOnClickListener {
            if(mMonth==1){
                mMonth = 12
                mYear -= 1
            }else{
                mMonth -=1
            }

            updateDateTextView()
            updateListener()
        }

        layNext.setOnClickListener {
            if(mMonth==12){
                mMonth = 1
                mYear += 1
            }else{
                mMonth +=1
            }

            updateDateTextView()
            updateListener()
        }

        txtDate.setOnClickListener {
            datePicker.create().show()
        }

        updateDateTextView()
    }

    private fun updateListener() {
        onDateSelectListener?.date(mDay, mMonth, mYear)
        onDateSelectListener?.dateString("$mYear-$mMonth-$mDay")
    }


    fun builder(
        onDateSelectListener: MyDatePicker.OnDateSelectListener?,
        mDay : Int = Constant.getDay(Constant.getCurrentDate()).toInt(),
        mMonth : Int= Constant.getMonthNumber(Constant.getCurrentDate()).toInt(),
        mYear : Int = Constant.getYear(Constant.getCurrentDate()).toInt()
    ): MonthPickerView{
        this.onDateSelectListener = onDateSelectListener
        this.mDay = mDay
        this.mMonth = mMonth
        this.mYear = mYear


        updateDateTextView()
        return this
    }

    fun setDay(day: Int):MonthPickerView{
        this.mDay = day
        updateDateTextView()
        return this
    }

    fun setMonth(month:Int):MonthPickerView{
        mMonth = month
        updateDateTextView()

        return this

    }

    fun setYear(year:Int):MonthPickerView{
        mYear = year
        updateDateTextView()

        return this

    }

    public fun setTextColor(color: Int){
        txtDate.setTextColor(color)

    }

    @SuppressLint("SetTextI18n")
    private fun updateDateTextView(){
        datePicker.mMonth = this.mMonth
        datePicker.mYear = this.mYear
        datePicker.mDay = this.mDay
        txtDate.text = "$mYear ${Constant.getMonthName("$mYear-$mMonth-$mDay")}"

        invalidate()
    }
}