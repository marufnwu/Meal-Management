package com.logicline.mydining.ui.custom.monthpicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.logicline.mydining.R
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast

class MonthPickerDialog private constructor(
    context: Context,
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

        val pickerView = findViewById<MonthPickerView>(R.id.monthPickerView)
        Log.d("MonthPickerDialog", "MonthPickerView found: ${pickerView != null}")

        pickerView?.initialize(
            preselectedIds = preselectedMonthId?.let { listOf(it) },
            config = {
                showSearch = true
                showTitle = true
                title = "Select Month"
            },
            onSelected = { month ->
                Log.d("MonthPickerDialog", "Month selected: ${month.name}")
                onMonthSelected(month)
                dismiss()
                dialogInstance = null
            }
        )
    }

    companion object {
        private var dialogInstance: MonthPickerDialog? = null

        fun show(
            context: Context,
            preselectedMonthId: Int? = null,
            onMonthSelected: (Month) -> Unit
        ) {
            context.shortToast("Opening month picker dialog")
            Log.d("MonthPickerDialog", "Show dialog called")

            if (dialogInstance?.isShowing == true) {
                Log.d("MonthPickerDialog", "Dialog already showing")
                return
            }

            dialogInstance = MonthPickerDialog(
                context = context,
                preselectedMonthId = preselectedMonthId,
                onMonthSelected = onMonthSelected
            )
            dialogInstance?.show()
        }
    }

    override fun dismiss() {
        super.dismiss()
        Log.d("MonthPickerDialog", "Dialog dismissed")
        dialogInstance = null
    }
}