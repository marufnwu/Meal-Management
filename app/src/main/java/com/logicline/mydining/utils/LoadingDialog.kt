package com.logicline.mydining.utils

import android.app.Activity
import android.app.Dialog
import android.util.Log
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.logicline.mydining.R


class LoadingDialog(var activity: Activity) {
    var dialog: Dialog? = null
    private var loadImageGif: Int = R.drawable.loading
    private var cancelable = false

    init {
        createDialog()
    }


    fun setCancelable(state: Boolean) {
        cancelable = state
    }

    private fun setGif(gif: Int){
        loadImageGif = gif

    }


    private fun createDialog(){
        if (loadImageGif != 0) {
            dialog = Dialog(activity)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            //inflate the layout
            dialog!!.setContentView(R.layout.custom_loading_dialog_layout)
            //setup cancelable, default=false
            dialog!!.setCancelable(cancelable)
            //get imageview to use in Glide
            val imageView = dialog!!.findViewById<ImageView>(R.id.custom_loading_imageView)

            //load gif and callback to imageview
            Glide.with(activity)
                .load(loadImageGif)
                .placeholder(loadImageGif)
                .centerCrop()
                .into(imageView)
        } else {
            Log.e(
                "LoadingDialog",
                "Erro, missing drawable of imageloading (gif), please, use setLoadImage(R.drawable.name)."
            )
        }
    }

    fun show() {
        if (!activity.isFinishing && dialog != null && !dialog!!.isShowing) {
            dialog!!.show()
        }
    }

    fun hide() {
        if (dialog != null && dialog!!.isShowing) {
           if(!activity.isFinishing && !activity.isDestroyed){
               dialog!!.dismiss()
           }
        }
    }

    val isLoading: Boolean
        get() = dialog!!.isShowing


}