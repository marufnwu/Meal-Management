package com.logicline.mydining.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.google.android.material.button.MaterialButton
import com.logicline.mydining.R

/**
 * A beautiful, reusable custom dialog for common use cases throughout the app.
 * Supports customizable icon, title, message, content layout, and buttons.
 */
class GenericDialog private constructor(
    private val context: Context,
    private val builder: Builder
) {
    private val dialog: Dialog = Dialog(context)
    private lateinit var iconView: ImageView
    private lateinit var titleView: TextView
    private lateinit var messageView: TextView
    private lateinit var contentContainer: FrameLayout
    private lateinit var positiveButton: MaterialButton
    private lateinit var negativeButton: MaterialButton

    init {
        setupDialog()
    }

    private fun setupDialog() {
        // Set up the dialog window properties
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(builder.cancelable)

        // Inflate and set the content view
        val view = LayoutInflater.from(context).inflate(R.layout.generic_dialog_layout, null)
        dialog.setContentView(view)

        // Initialize views
        iconView = view.findViewById(R.id.dialog_icon)
        titleView = view.findViewById(R.id.dialog_title)
        messageView = view.findViewById(R.id.dialog_message)
        contentContainer = view.findViewById(R.id.dialog_content)
        positiveButton = view.findViewById(R.id.positive_button)
        negativeButton = view.findViewById(R.id.negative_button)

        // Apply builder properties
        setupIcon()
        setupTitle()
        setupMessage()
        setupContent()
        setupButtons()

        // Set dialog width to match parent
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupIcon() {
        builder.iconResId?.let {
            iconView.setImageResource(it)
            iconView.visibility = View.VISIBLE
        } ?: run {
            iconView.visibility = View.GONE
        }
    }

    private fun setupTitle() {
        builder.title?.let {
            titleView.text = it
            titleView.visibility = View.VISIBLE
        } ?: run {
            titleView.visibility = View.GONE
        }
    }

    private fun setupMessage() {
        builder.message?.let {
            messageView.text = it
            messageView.visibility = View.VISIBLE
        } ?: run {
            messageView.visibility = View.GONE
        }
    }

    private fun setupContent() {
        builder.contentLayoutResId?.let { layoutResId ->
            val contentView = LayoutInflater.from(context).inflate(layoutResId, contentContainer, false)
            contentContainer.addView(contentView)
            contentContainer.visibility = View.VISIBLE
        } ?: run {
            contentContainer.visibility = View.GONE
        }
    }

    private fun setupButtons() {
        // Positive button
        builder.positiveButtonText?.let { text ->
            positiveButton.text = text
            positiveButton.visibility = View.VISIBLE
            positiveButton.setOnClickListener {
                builder.positiveButtonClickListener?.onClick(this)
                if (builder.autoDismiss) {
                    dismiss()
                }
            }
        } ?: run {
            positiveButton.visibility = View.GONE
        }

        // Negative button
        builder.negativeButtonText?.let { text ->
            negativeButton.text = text
            negativeButton.visibility = View.VISIBLE
            negativeButton.setOnClickListener {
                builder.negativeButtonClickListener?.onClick(this)
                if (builder.autoDismiss) {
                    dismiss()
                }
            }
        } ?: run {
            negativeButton.visibility = View.GONE
        }
    }

    /**
     * Shows the dialog
     */
    fun show() {
        dialog.show()
    }

    /**
     * Dismisses the dialog
     */
    fun dismiss() {
        dialog.dismiss()
    }

    /**
     * Checks if the dialog is showing
     */
    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    /**
     * Builder class for creating GenericDialog instances
     */
    class Builder(private val context: Context) {
        var iconResId: Int? = null
            private set
        var title: String? = null
            private set
        var message: String? = null
            private set
        var contentLayoutResId: Int? = null
            private set
        var positiveButtonText: String? = null
            private set
        var negativeButtonText: String? = null
            private set
        var positiveButtonClickListener: OnClickListener? = null
            private set
        var negativeButtonClickListener: OnClickListener? = null
            private set
        var cancelable: Boolean = true
            private set
        var autoDismiss: Boolean = true
            private set

        /**
         * Sets the dialog icon
         */
        fun setIcon(@DrawableRes iconResId: Int): Builder {
            this.iconResId = iconResId
            return this
        }

        /**
         * Sets the dialog title
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Sets the dialog message
         */
        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        /**
         * Sets a custom layout for the dialog content area
         */
        fun setContentView(@LayoutRes layoutResId: Int): Builder {
            this.contentLayoutResId = layoutResId
            return this
        }

        /**
         * Sets the text and click listener for the positive button
         */
        fun setPositiveButton(text: String, listener: OnClickListener? = null): Builder {
            this.positiveButtonText = text
            this.positiveButtonClickListener = listener
            return this
        }

        /**
         * Sets the text and click listener for the negative button
         */
        fun setNegativeButton(text: String, listener: OnClickListener? = null): Builder {
            this.negativeButtonText = text
            this.negativeButtonClickListener = listener
            return this
        }

        /**
         * Sets whether the dialog is cancelable by tapping outside or pressing back
         */
        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        /**
         * Sets whether the dialog should auto-dismiss when a button is clicked
         */
        fun setAutoDismiss(autoDismiss: Boolean): Builder {
            this.autoDismiss = autoDismiss
            return this
        }

        /**
         * Creates a GenericDialog with the configured options
         */
        fun build(): GenericDialog {
            return GenericDialog(context, this)
        }

        /**
         * Creates and shows a GenericDialog with the configured options
         */
        fun show(): GenericDialog {
            val dialog = build()
            dialog.show()
            return dialog
        }
    }

    fun <T : View> findViewById(id: Int): T? {
        return dialog.findViewById(id)
    }

    /**
     * Interface for button click listeners
     */
    interface OnClickListener {
        fun onClick(genericDialog: GenericDialog)
    }
}