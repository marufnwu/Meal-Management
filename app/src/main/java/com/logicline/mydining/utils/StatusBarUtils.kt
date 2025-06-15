package com.logicline.mydining.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Extension function to set status bar color with lifecycle awareness
 * @param colorResId The color resource ID to set
 * @param isLight Whether to use light status bar (dark icons) or not
 */
fun Fragment.setStatusBarColorWithLifecycle(@ColorRes colorResId: Int, isLight: Boolean = false) {
    // Only proceed if fragment is at least CREATED state
    if (!this.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) return

    val activity = requireActivity()
    val window = activity.window
    val color = ContextCompat.getColor(requireContext(), colorResId)

    // Create a lifecycle observer to ensure color is applied when fragment is resumed
    val observer = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            applyStatusBarColor(activity, color, isLight)
        }
    }

    // Apply color immediately
    applyStatusBarColor(activity, color, isLight)

    // Register the lifecycle observer
    viewLifecycleOwner.lifecycle.addObserver(observer)
}

/**
 * Helper function to apply status bar color
 */
private fun applyStatusBarColor(activity: Activity, color: Int, isLight: Boolean) {
    val window = activity.window

    // Set the status bar color
    window.statusBarColor = color

    // Handle status bar icon color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Android 11+ (API 30+)
        window.insetsController?.let { controller ->
            if (isLight) {
                controller.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                controller.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Android 6.0 (API 23) to Android 10 (API 29)
        val decorView = window.decorView
        var flags = decorView.systemUiVisibility

        flags = if (isLight) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }

        decorView.systemUiVisibility = flags
    }
}