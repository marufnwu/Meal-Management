package com.logicline.mydining.ui.custom.monthpicker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.logicline.mydining.MyApplication
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.databinding.ViewMonthSelectorBinding

class MonthSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding : ViewMonthSelectorBinding =
        ViewMonthSelectorBinding.inflate(LayoutInflater.from(context), this)
    private var monthRepository: MonthRepository? = null
    private var selectedMonth: Month? = null
    private var onMonthSelectedListener: ((Month) -> Unit)? = null

    init {
        initialize(MonthRepository((context.applicationContext as MyApplication).myApi))
           binding.root.setOnClickListener {
               showMonthPicker()
        }
    }

    private fun showMonthPicker() {
        monthRepository?.let { repo ->
            MonthPickerDialog.show(
                context = context,
                preselectedMonthId = selectedMonth?.id,
            ) { month ->
                setSelectedMonth(month)
                onMonthSelectedListener?.invoke(month)
            }
        }
    }

    fun initialize(
        monthRepository: MonthRepository,
        initialMonth: Month? = null,
        onMonthSelected: ((Month) -> Unit)? = null
    ) {
        this.monthRepository = monthRepository
        this.onMonthSelectedListener = onMonthSelected

    }

    fun setSelectedMonth(month: Month) {
        selectedMonth = month
        binding.tvMonthName.text = month.name
    }

    fun getSelectedMonth(): Month? = selectedMonth
}