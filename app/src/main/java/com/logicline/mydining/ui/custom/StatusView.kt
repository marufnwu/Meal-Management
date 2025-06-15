package com.logicline.mydining.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.logicline.mydining.R

/**
 * A custom view that displays different states (empty, error, info) with
 * customizable image, message, and action buttons.
 */
class StatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val statusImage: ImageView
    private val statusMessage: TextView
    private val positiveButton: Button
    private val negativeButton: Button

    // Listener interfaces for button clicks
    interface OnPositiveButtonClickListener {
        fun onPositiveButtonClick()
    }

    // Listeners that can be set by the client
    private var positiveButtonClickListener: OnPositiveButtonClickListener? = null

    // Enum for different states
    enum class StatusType {
        EMPTY, ERROR, INFO
    }

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.layout_status, this, true)

        // Hide status view by default
        findViewById<View>(R.id.status_container).visibility = View.GONE

        // Initialize views
        statusImage = findViewById(R.id.status_image)
        statusMessage = findViewById(R.id.status_message)
        positiveButton = findViewById(R.id.positive_button)
        negativeButton = findViewById(R.id.negative_button)

        // Set button click listeners
        positiveButton.setOnClickListener {
            positiveButtonClickListener?.onPositiveButtonClick()
        }

        negativeButton.setOnClickListener {
            // No-op, handled in setNegativeButton
        }

        // Parse custom attributes if available
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.StatusView)
            try {
                val messageText = typedArray.getString(R.styleable.StatusView_statusMessage)
                val imageResId = typedArray.getResourceId(R.styleable.StatusView_statusImage, 0)
                val positiveButtonText =
                    typedArray.getString(R.styleable.StatusView_positiveButtonText)
                val negativeButtonText =
                    typedArray.getString(R.styleable.StatusView_negativeButtonText)

                // Set initial values from attributes
                messageText?.let { text -> statusMessage.text = text }
                if (imageResId != 0) {
                    statusImage.setImageResource(imageResId)
                }

                positiveButtonText?.let { text ->
                    positiveButton.text = text
                    positiveButton.isVisible = true
                }

                negativeButtonText?.let { text ->
                    negativeButton.text = text
                    negativeButton.isVisible = true
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    /**
     * Sets the status type (EMPTY, ERROR, INFO) which determines the default image.
     *
     * @param type The status type to display
     * @param message Optional custom message to display
     */
    fun setStatus(type: StatusType, message: String? = null) : StatusView {
        val imageResId = when (type) {
            StatusType.EMPTY -> R.drawable.empty
            StatusType.ERROR -> R.drawable.warning
            StatusType.INFO -> R.drawable.informing
        }

        val defaultMessage = when (type) {
            StatusType.EMPTY -> context.getString(R.string.empty_state_message)
            StatusType.ERROR -> context.getString(R.string.error_state_message)
            StatusType.INFO -> context.getString(R.string.info_state_message)
        }

        statusImage.setImageResource(imageResId)
        statusMessage.text = message ?: defaultMessage

        return this
    }

    /**
     * Set a custom image for the status view.
     *
     * @param resId The drawable resource ID for the image
     */
    fun setStatusImage(@DrawableRes resId: Int) {
        statusImage.setImageResource(resId)
    }

    /**
     * Set a custom message for the status view.
     *
     * @param message The message to display
     */
    fun setStatusMessage(message: String) {
        statusMessage.text = message
    }

    /**
     * Configure the positive action button.
     *
     * @param text The button text
     * @param isVisible Whether the button should be visible
     * @param onClick Lambda function that will be called when the button is clicked
     */
    fun setPositiveButton(
        text: String? = null,
        isVisible: Boolean = true,
        onClick: (() -> Unit)? = null
    ) : StatusView {
        text?.let {
            positiveButton.text = it
        }
        positiveButton.visibility = if (isVisible) View.VISIBLE else View.GONE

        // If a click listener was provided, set it directly on the button
        if (onClick != null) {
            positiveButton.setOnClickListener { onClick() }
        } else {
            positiveButton.setOnClickListener(null)
        }

        return this
    }

    /**
     * Configure the negative action button.
     *
     * @param text The button text
     * @param isVisible Whether the button should be visible
     * @param onClick Lambda function that will be called when the button is clicked
     */
    fun setNegativeButton(
        text: String? = null,
        isVisible: Boolean = true,
        onClick: (() -> Unit)? = null
    ) : StatusView {
        text?.let {
            negativeButton.text = it
        }
        negativeButton.visibility = if (isVisible) View.VISIBLE else View.GONE

        // If a click listener was provided, set it directly on the button
        if (onClick != null) {
            negativeButton.setOnClickListener { onClick() }
        } else {
            negativeButton.setOnClickListener(null)
        }

        return this
    }

    /**
     * Hide the positive button.
     */
    fun hidePositiveButton() {
        positiveButton.visibility = View.GONE
    }

    /**
     * Hide the negative button.
     */
    fun hideNegativeButton() {
        negativeButton.visibility = View.GONE
    }

    /**
     * Controls the visibility of this StatusView.
     * When shown, it will hide any content views that were added.
     * When hidden, it will show any content views that were added.
     *
     * @param isVisible true to show the status view, false to hide it
     */
    fun setStatusViewVisible(isVisible: Boolean) : StatusView {
        if (isVisible) {
            findViewById<View>(R.id.status_container).visibility = View.VISIBLE
            // Hide all content views
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.id != R.id.status_container) {
                    child.visibility = View.GONE
                }
            }
        } else {
            findViewById<View>(R.id.status_container).visibility = View.GONE
            // Show all content views
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child.id != R.id.status_container) {
                    child.visibility = View.VISIBLE
                }
            }
        }
        return this
    }

    /**
     * Shows the status view and hides content views.
     */
    fun showStatusView() {
        setStatusViewVisible(true)
    }

    /**
     * Hides the status view and shows content views.
     */
    fun hideStatusView() {
        setStatusViewVisible(false)
    }

    /**
     * Set the visibility of a specific child view by its ID.
     *
     * @param childId the ID of the child view
     * @param isVisible true to show the child, false to hide it
     */
    fun setChildViewVisible(childId: Int, isVisible: Boolean) {
        findViewById<View>(childId)?.let { child ->
            child.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    /**
     * Add a content view to the StatusView.
     * This allows adding additional views that will be managed by the StatusView's visibility logic.
     *
     * @param view the view to add
     * @param params the layout parameters for the view
     */
    fun addContentView(view: View, params: FrameLayout.LayoutParams) {
        addView(view, params)
        // Hide the content view if status view is visible
        if (findViewById<View>(R.id.status_container).visibility == View.VISIBLE) {
            view.visibility = View.GONE
        }
    }
}