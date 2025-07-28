package com.logicline.mydining.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.logicline.mydining.R


class LanguageSelectorDialog(val builder: Builder) {
    //private var activity: Activity = builder.activity

    private var dialog : Dialog = builder.dialog

    class Builder(val context: Context){
        var dialog = Dialog(context)
        var onSave : ((Dialog, Constant.LANGUAGE)->Unit) ? = null
        var onCancel : ((LanguageSelectorDialog)->Unit)? = null

        lateinit var languageSelectorDialog: LanguageSelectorDialog
        fun OnSave( onSave:(Dialog, Constant.LANGUAGE)->Unit){
            this.onSave = onSave
        }

        fun OnCancel(onCancel:((LanguageSelectorDialog)->Unit)?) : Builder{
            this.onCancel = onCancel
            return this
        }

        fun build() :LanguageSelectorDialog{
            languageSelectorDialog = LanguageSelectorDialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_choose_language_layout)

            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val window = dialog.window
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


            val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGrpLang)

            when(LangUtils.getLanguage(context)){
                Constant.LANGUAGE.en_US.name -> radioGroup.check(R.id.radioBtnEn)
                Constant.LANGUAGE.bn.name -> radioGroup.check(R.id.radioBtnBn)
            }


            var selectedlang : Constant.LANGUAGE? = null

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    R.id.radioBtnEn -> selectedlang = Constant.LANGUAGE.en_US
                    R.id.radioBtnBn -> selectedlang = Constant.LANGUAGE.bn
                }
            }


            dialog.findViewById<Button>(R.id.btnCancel)
                .setOnClickListener {
                    onCancel?.invoke(languageSelectorDialog)
                    dialog.hide()
            }

            dialog.findViewById<Button>(R.id.btnSave)
                .setOnClickListener {
                    selectedlang?.let {
                        LangUtils.changeLanguage(context, it)

                        Constant.getActivity(context)?.let {
                            it.finish()
                            context.startActivity(it.intent)
                            it.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                        }


                    }

                    dialog.hide()
                }

            return languageSelectorDialog
        }
    }

    fun show(){
        Constant.getActivity(builder.context)?.let {
            if(!it.isFinishing && !it.isDestroyed){
                if(dialog.isShowing){
                    dialog.dismiss()
                }

                dialog.show()
            }
        }


    }

    fun dismiss(){
        Constant.getActivity(builder.context)?.let {
            if(!it.isFinishing && !it.isDestroyed){
                if(dialog.isShowing){
                    dialog.hide()
                }

            }
        }


    }



}