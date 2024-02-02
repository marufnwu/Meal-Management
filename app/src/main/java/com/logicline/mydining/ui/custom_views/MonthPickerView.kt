package com.logicline.mydining.ui.custom_views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.logicline.mydining.R
import com.logicline.mydining.models.Month
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.ListNavigator
import com.logicline.mydining.utils.MyApplication
import com.logicline.mydining.utils.MyDatePicker
import com.logicline.mydining.utils.MyExtensions.shortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthPickerView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private var force: Boolean = false
    private var navigator: ListNavigator? = null
    public var onDateSelectListener: MyDatePicker.OnDateSelectListener? = null
    private lateinit var view: View
    private lateinit var txtDate: TextView
    private lateinit var layPrevious: RelativeLayout
    private lateinit var layNext: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var monthNavigator : View
    private lateinit var monthName : TextView

    private lateinit var datePicker : MyDatePicker

    var mDay: Int? = Constant.getDay(Constant.getCurrentDate()).toInt()
    var mMonth: Int? = Constant.getMonthNumber(Constant.getCurrentDate()).toInt()
    var mYear: Int? = Constant.getYear(Constant.getCurrentDate()).toInt()
    var monthId: Int? = null



    init {

    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        view = inflate(context, R.layout.layout_date_picker_view, this)
        txtDate = view.findViewById(R.id.txtDate)
        layPrevious = view.findViewById(R.id.layPrevious)
        layNext = view.findViewById(R.id.layNext)
        progressBar = findViewById(R.id.progress)
        monthNavigator = findViewById(R.id.monthNavigator)
        monthName = findViewById(R.id.tvMonthName)

        Toast.makeText(context, force.toString(), Toast.LENGTH_SHORT).show()

        if(force){
            monthNavigator.visibility = GONE
            monthName.visibility = View.VISIBLE
            if(mYear !=null && mMonth !=null){
                monthName.text = "$mYear ${Constant.getMonthName(mMonth!!.toInt())}"
            }else if(monthId!=null){
                //set month id name
            }



        }else{
            if(Constant.getMessType()==Constant.MessType.MANUALLY){
                getAllMonths()
            }else{
                datePicker = MyDatePicker(context, object : MyDatePicker.OnDateSelectListener {
                    override fun date(date: Int?, month: Int?, year: Int?) {
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
                bindNavigator()
            }

            updateDateTextView()
        }
    }

    private fun getAllMonths() {
        monthNavigator.visibility = GONE
        progressBar.visibility = VISIBLE
        (context.applicationContext as MyApplication)
            .myApi
            .getAllMonthList()
            .enqueue(object : Callback<ServerResponse<List<Month>>> {
                override fun onResponse(
                    call: Call<ServerResponse<List<Month>>>,
                    response: Response<ServerResponse<List<Month>>>
                ) {
                    progressBar.visibility  = GONE
                    if(response.isSuccessful && response.body()!=null){
                        val body = response.body()!!
                        if(body.error){
                            context.shortToast(body.msg)
                            return
                        }


                        addMonthListNavigator(body.data)
                    }
                }

                override fun onFailure(call: Call<ServerResponse<List<Month>>>, t: Throwable) {
                    context.shortToast("Something Went Wrong")
                    progressBar.visibility = GONE

                }

            })
    }

    private fun addMonthListNavigator(data: List<Month>?) {
        if(data!=null){
            navigator = ListNavigator(data)
            if(data.size>0){
                bindNavigator()
            }
        }
    }

    private fun bindNavigator() {


        monthNavigator.visibility  = VISIBLE

        layPrevious.setOnClickListener {
            if(Constant.getMessType()==Constant.MessType.MANUALLY){
                navigator?.getPreviousItem()
                onDateSelectListener?.month(navigator?.getCurrentItem()?.id!!)
            }else{
                if(mMonth==1){
                    mMonth = 12
                    mYear = mYear?.minus(1)
                }else{
                    mMonth = mMonth?.minus(1)
                }

                onDateSelectListener?.date(mDay, mMonth, mYear)
            }



            updateDateTextView()

        }

        layNext.setOnClickListener {

            if(Constant.getMessType()==Constant.MessType.MANUALLY){
                navigator?.getNextItem()
                onDateSelectListener?.month(navigator?.getCurrentItem()?.id!!)
            }else{
                if(mMonth==12){
                    mMonth = 1
                    mYear = mYear?.plus(1)
                }else{
                    mMonth = mMonth?.plus(1)
                }
                onDateSelectListener?.date(mDay, mMonth, mYear)

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

        onDateSelectListener?.dateString("$mYear-$mMonth-$mDay")
    }


    fun builder(
        onDateSelectListener: MyDatePicker.OnDateSelectListener?,
        mDay : Int? = Constant.getDay(Constant.getCurrentDate()).toInt(),
        mMonth : Int?= Constant.getMonthNumber(Constant.getCurrentDate()).toInt(),
        mYear : Int? = Constant.getYear(Constant.getCurrentDate()).toInt(),
        monthId : Int? = null,
        force : Boolean = false
    ): MonthPickerView{
        this.onDateSelectListener = onDateSelectListener
        this.mDay = mDay
        this.mMonth = mMonth
        this.mYear = mYear
        this.monthId = monthId
        this.force = force

        initView()


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


        if(Constant.getMessType()==Constant.MessType.MANUALLY){
            if(navigator!=null){
               navigator?.getCurrentItem()?.name?.let {
                   txtDate.text = it
               }
            }

        }else{
            datePicker.mMonth = this.mMonth
            datePicker.mYear = this.mYear
            datePicker.mDay = this.mDay
            txtDate.text = "$mYear ${Constant.getMonthName("$mYear-$mMonth-$mDay")}"
        }

        invalidate()
    }
}