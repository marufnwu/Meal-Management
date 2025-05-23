package com.logicline.mydining.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.withStyledAttributes
import com.google.android.material.card.MaterialCardView
import com.logicline.mydining.R

/**
 * A Material 3 styled field container with an icon.
 * Add any child view directly in XML layout.
 */
class MaterialIconField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val iconView: ImageView
    private val iconContainer: MaterialCardView
    private val contentContainer: FrameLayout
    private val mainContainer: MaterialCardView

    init {
        orientation = VERTICAL

        // Inflate the base layout
        val root = LayoutInflater.from(context)
            .inflate(R.layout.view_material_icon, this, true)

        // Get references to views
        iconView = findViewById(R.id.iconView)
        iconContainer = findViewById(R.id.iconContainer)
        contentContainer = findViewById(R.id.contentContainer)
        mainContainer = findViewById(R.id.mainContainer)

        // Apply custom attributes
        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.MaterialIconField) {
                // Set icon
                val iconResId = getResourceId(R.styleable.MaterialIconField_fieldIcon, 0)
                if (iconResId != 0) {
                    iconView.setImageResource(iconResId)
                }

                // Set icon tint
                val iconTint = getColorStateList(R.styleable.MaterialIconField_fieldIconTint)
                if (iconTint != null) {
                    iconView.imageTintList = iconTint
                }

                // Set icon background color
                val iconBgColor = getColorStateList(R.styleable.MaterialIconField_iconBackgroundColor)
                if (iconBgColor != null) {
                    iconContainer.setCardBackgroundColor(iconBgColor)
                }
            }
        }
    }

    /**
     * We need to override this to intercept child views from XML
     */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (contentContainer == null) {
            // If contentContainer isn't initialized yet, we're still in initialization phase
            super.addView(child, index, params)
        } else {
            // Add child view to the content container instead of directly to this view
            contentContainer.addView(child, params)
        }
    }

    /**
     * Set the field icon
     */
    fun setIcon(@DrawableRes iconResId: Int) {
        iconView.setImageResource(iconResId)
    }

    /**
     * Set the icon tint color
     */
    fun setIconTint(colorStateList: android.content.res.ColorStateList?) {
        iconView.imageTintList = colorStateList
    }

    /**
     * Set the icon background color
     */
    fun setIconBackgroundColor(colorStateList: android.content.res.ColorStateList?) {
        iconContainer.setCardBackgroundColor(colorStateList)
    }
}