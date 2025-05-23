package com.logicline.mydining.ui.custom.monthpicker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.logicline.mydining.R
import com.logicline.mydining.data.models.Month

class MonthPickerBottomSheet : BottomSheetDialogFragment() {
    private var onMonthSelected: ((Month) -> Unit)? = null
    private var preselectedMonthId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_month_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set title if needed
        dialog?.setTitle("Select a Month")

        // Get selected month ID from arguments or saved instance state
        val monthId = arguments?.getInt(ARG_MONTH_ID, -1)?.takeIf { it != -1 }
            ?: savedInstanceState?.getInt(ARG_MONTH_ID, -1)?.takeIf { it != -1 }
            ?: preselectedMonthId

        val pickerView = view.findViewById<MonthPickerView>(R.id.monthPickerView)
        Log.d(TAG, "MonthPickerView found: ${pickerView != null}")

        pickerView?.initialize(
            preselectedIds = monthId?.let { listOf(it) },
            config = {
                showSearch = true
                showTitle = true
                title = "Select Month"
            },
            onSelected = { month ->
                Log.d(TAG, "Month selected: ${month.name}")
                onMonthSelected?.invoke(month)
                dismiss()
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the selected month ID for configuration changes
        arguments?.getInt(ARG_MONTH_ID, -1)?.takeIf { it != -1 }?.let {
            outState.putInt(ARG_MONTH_ID, it)
        }
    }

    companion object {
        private const val TAG = "MonthPickerBottomSheet"
        private const val ARG_MONTH_ID = "month_id"

        // Use a nullable instance variable
        private var instance: MonthPickerBottomSheet? = null

        fun show(
            fragmentManager: FragmentManager,
            preselectedMonthId: Int? = null,
            onMonthSelected: (Month) -> Unit
        ) {
            // Check if we already have a fragment in the manager
            val currentFragment = fragmentManager.findFragmentByTag(TAG) as? MonthPickerBottomSheet

            if (currentFragment?.isVisible == true) {
                // Already showing, just update the callback and preselected ID
                currentFragment.onMonthSelected = onMonthSelected
                currentFragment.preselectedMonthId = preselectedMonthId
                return
            }

            // If we have an instance but it's not showing, or we don't have one, create/reuse
            val bottomSheet = currentFragment ?: instance ?: MonthPickerBottomSheet().also {
                instance = it
            }

            // Update the callback and preselected ID
            bottomSheet.onMonthSelected = onMonthSelected
            bottomSheet.preselectedMonthId = preselectedMonthId

            // Set arguments
            bottomSheet.arguments = Bundle().apply {
                preselectedMonthId?.let { putInt(ARG_MONTH_ID, it) }
            }

            // Show the bottom sheet
            bottomSheet.show(fragmentManager, TAG)
        }
    }

    override fun dismiss() {
        super.dismiss()
        Log.d(TAG, "Bottom sheet dismissed")
        // Don't set instance to null, we're keeping it for reuse
    }

    override fun onDestroy() {
        super.onDestroy()
        // Only clear the instance when the fragment is destroyed
        // (typically when the host activity is destroyed)
        if (instance === this) {
            instance = null
        }
    }
}