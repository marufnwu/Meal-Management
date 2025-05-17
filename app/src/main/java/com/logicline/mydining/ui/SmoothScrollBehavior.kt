package com.logicline.mydining.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class SmoothScrollBehavior(context: Context, attrs: AttributeSet?) : AppBarLayout.Behavior(context, attrs) {

    init {
        // Disable AppBarLayout drag directly
        setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return true
            }
        })
    }

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        // Only handle vertical scrolls
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        // Apply smoothing for slow scrolls
        val dampedDy = when {
            abs(dy) < 10 -> (dy * 0.7).toInt()  // Dampen slow scrolls more
            abs(dy) < 30 -> (dy * 0.85).toInt() // Medium dampen for medium scrolls
            else -> dy                          // No dampen for fast scrolls
        }

        // Call the parent with our dampened value
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dampedDy, consumed, type)
    }
}