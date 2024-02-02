package com.logicline.mydining.utils

import android.R
import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.logicline.mydining.databinding.DialogSelectMessTypeDialogBinding


class DialogMaker {

     class SelectMessTypeDialog(private val activity : Activity, private val currentType:Int, private val onSubmit: (dialog:Dialog, type:Int) -> Unit){

         private var spinnerItems = arrayOf("None", "Auto", "Manual")
         private var selectedItem : Int? = null
         private lateinit var dialog: AlertDialog

        init {
            createDialog()
        }

        private fun createDialog() {
            val dialogBinding = DialogSelectMessTypeDialogBinding.inflate(LayoutInflater.from(activity));
            val builder= AlertDialog.Builder(activity)
                .setCancelable(true)
                .setView(dialogBinding.root)
            dialog = builder.create()

            dialogBinding.btnSubmit.setOnClickListener {
                if(selectedItem!=null){
                    onSubmit.invoke(dialog, selectedItem!!);
                }else{
                    Toast.makeText(activity, "Please select your preferred mess type", Toast.LENGTH_SHORT).show()
                }

            }
            val adapter: ArrayAdapter<String> = ArrayAdapter(activity, R.layout.simple_spinner_item, spinnerItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerType.adapter = adapter
            dialogBinding.spinnerType.setSelection(currentType)


            dialogBinding.spinnerType.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2==0){
                        selectedItem  =null
                        return
                    }
                    selectedItem = p2
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    selectedItem = null
                }

            }
        }

        fun show(){
            if(!dialog.isShowing && !activity.isFinishing && !activity.isDestroyed){
                dialog.show()
            }
        }
    }
}