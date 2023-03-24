package com.logicline.mydining.utils

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner

object MyExtensions {

    fun Context.shortToast(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    fun Context.longToast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext as LifecycleOwner
        } else {
            null
        }
    }



}