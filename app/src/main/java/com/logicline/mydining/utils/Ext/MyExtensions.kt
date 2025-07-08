package com.logicline.mydining.utils.Ext

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.logicline.mydining.data.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object MyExtensions {

    fun Context.shortToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.longToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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

    inline fun <T> Flow<DataState<T>>.collectState(
        lifecycleOwner: LifecycleOwner,
        crossinline onSuccess: (T?) -> Unit = {},
        crossinline onError: (String?) -> Unit = {},
        crossinline onLoading: () -> Unit = {},
        crossinline onIdle: () -> Unit = {}
    ) {
        lifecycleOwner.lifecycleScope.launch {
            this@collectState.collect { state ->
                when (state) {
                    is DataState.Loading -> onLoading()
                    is DataState.Success -> onSuccess(state.data)
                    is DataState.Error -> onError(state.message)
                    else -> onIdle()
                }
            }
        }
    }

    inline fun <T> DataState<T>.handle(
        onLoading: () -> Unit = {},
        onSuccess: (T?) -> Unit = {},
        onError: (String?) -> Unit = {},
        onIdle: () -> Unit = {}
    ) {
        when (this) {
            is DataState.Loading -> onLoading()
            is DataState.Success -> onSuccess(data)
            is DataState.Error -> onError(message)
            else -> onIdle()
        }
    }

    fun String.capitalizeFirstChar(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } + this.drop(1).lowercase()
    }

}