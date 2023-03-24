package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.logicline.mydining.R
import com.logicline.mydining.utils.Constant



class MainBottomSheet : BottomSheetDialogFragment() {

    companion object{
        fun newInstance(): MainBottomSheet {
            return MainBottomSheet()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)

        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.isDraggable = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        return dialog
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.setContentView(R.layout.layout_nav_bottom_sheet)


        val privacyPolicy = dialog.findViewById<LinearLayout>(R.id.layoutPrivacyPolicy)
        val terms = dialog.findViewById<LinearLayout>(R.id.layoutTerms)
        val layoutContact = dialog.findViewById<LinearLayout>(R.id.layoutContact)



        privacyPolicy.setOnClickListener {
            Constant.openPrivacyPolicy(requireContext())
        }


        terms.setOnClickListener {
            Constant.openTermsAndCondition(requireContext())
        }
        layoutContact.setOnClickListener {
            Constant.openContactUs(requireContext())
        }

    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialogTheme
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }


}