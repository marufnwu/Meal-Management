package com.logicline.mydining.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.logicline.mydining.R

/**
 * A custom EditText with Material 3 styling including an icon in a card container
 */
class MaterialIconEditTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val editText: EditText
    private val iconView: ImageView
    private val iconContainer: MaterialCardView
    private val mainContainer: MaterialCardView

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.view_material_icon_edit_text, this, true)

        // Get references to views
        editText = findViewById(R.id.editText)
        iconView = findViewById(R.id.iconView)
        iconContainer = findViewById(R.id.iconContainer)
        mainContainer = findViewById(R.id.mainContainer)

        // Apply custom attributes
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MaterialIconEditTextField, defStyleAttr, 0
            )

            try {
                // Set icon
                val iconResId = typedArray.getResourceId(
                    R.styleable.MaterialIconEditTextField_fieldIcon, 0
                )
                if (iconResId != 0) {
                    iconView.setImageResource(iconResId)
                }

                // Set icon tint
                val iconTint = typedArray.getColorStateList(
                    R.styleable.MaterialIconEditTextField_fieldIconTint
                )
                if (iconTint != null) {
                    iconView.imageTintList = iconTint
                }

                // Apply EditText attributes
                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_hint)) {
                    editText.hint = typedArray.getString(R.styleable.MaterialIconEditTextField_android_hint)
                }

                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_inputType)) {
                    editText.inputType = typedArray.getInt(
                        R.styleable.MaterialIconEditTextField_android_inputType,
                        android.text.InputType.TYPE_CLASS_TEXT
                    )
                }

                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_text)) {
                    editText.setText(typedArray.getString(R.styleable.MaterialIconEditTextField_android_text))
                }

                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_maxLines)) {
                    editText.maxLines = typedArray.getInt(
                        R.styleable.MaterialIconEditTextField_android_maxLines, 1
                    )
                }

                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_maxLength)) {
                    val maxLength = typedArray.getInt(R.styleable.MaterialIconEditTextField_android_maxLength, -1)
                    if (maxLength >= 0) {
                        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
                    }
                }

                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_digits)) {
                    editText.keyListener = android.text.method.DigitsKeyListener.getInstance(
                        typedArray.getString(R.styleable.MaterialIconEditTextField_android_digits).toString()
                    )
                }

                if (typedArray.hasValue(R.styleable.MaterialIconEditTextField_android_imeOptions)) {
                    editText.imeOptions = typedArray.getInt(
                        R.styleable.MaterialIconEditTextField_android_imeOptions,
                        android.view.inputmethod.EditorInfo.IME_NULL
                    )
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    // Expose EditText functionality

    var text: String
        get() = editText.text.toString()
        set(value) = editText.setText(value)

    var hint: CharSequence?
        get() = editText.hint
        set(value) {
            editText.hint = value
        }

    var inputType: Int
        get() = editText.inputType
        set(value) {
            editText.inputType = value
        }

    fun addTextChangedListener(watcher: TextWatcher) {
        editText.addTextChangedListener(watcher)
    }

    fun setSelection(index: Int) {
        editText.setSelection(index)
    }

    fun clear(){
        editText.text.clear()
    }
    fun setIcon(@DrawableRes iconResId: Int) {
        iconView.setImageResource(iconResId)
    }

    // Get direct access to components if needed
    fun getEditText(): EditText = editText
}