package com.logicline.mydining.ui.custom.monthpicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.logicline.mydining.R
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.repository.MonthRepository

class MonthPickerDialog private constructor(
    context: Context,
    private val monthRepository: MonthRepository,
    private val preselectedMonthId: Int? = null,
    private val onMonthSelected: (Month) -> Unit
) : Dialog(context) {

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_month_picker)
        setTitle("Select a Month")

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

//        val pickerView = findViewById<MonthPickerView>(R.id.monthPickerView)
//        pickerView?.initialize(
//            monthRepository = monthRepository,
//            preselectedId = preselectedMonthId,
//            onSelected = {
//                onMonthSelected(it)
//                dismiss()
//                dialogInstance = null
//            }
//        )
    }

    companion object {
        private var dialogInstance: MonthPickerDialog? = null

        fun show(
            context: Context,
            monthRepository: MonthRepository,
            preselectedMonthId: Int? = null,
            onMonthSelected: (Month) -> Unit
        ) {
            if (dialogInstance?.isShowing == true) return

            dialogInstance = MonthPickerDialog(
                context = context,
                monthRepository = monthRepository,
                preselectedMonthId = preselectedMonthId,
                onMonthSelected = onMonthSelected
            )
            dialogInstance?.show()
        }
    }

    override fun dismiss() {
        super.dismiss()
        dialogInstance = null
    }
}

