package com.logicline.mydining.data.models.response

import com.google.gson.annotations.SerializedName

class ServerResponse<T> (
    @SerializedName("error") var error: Boolean = true,
    @SerializedName("message") var msg: String = "",
    @SerializedName("data") var data: T? = null,
    @SerializedName("errors") var errors: List<String>? = null,
    @SerializedName("error_code") var errorCode: String? = null
) {
    val success: Boolean
        get() = !error
    
    val message: String
        get() = msg

    /**
     * Returns all errors as a single string, separated by the specified delimiter.
     * If there are no errors, returns an empty string.
     *
     * @param delimiter The string to use as a delimiter between errors
     * @return A string containing all errors concatenated with the delimiter
     */
    fun getErrorsAsString(delimiter: String = "\n"): String {
        return errors?.joinToString(delimiter) ?: ""
    }

    /**
     * Returns the first error in the errors list.
     * If there are no errors, returns null.
     *
     * @return The first error message or null
     */
    fun getFirstError(): String? {
        return errors?.firstOrNull()
    }

    /**
     * Returns the last error in the errors list.
     * If there are no errors, returns null.
     *
     * @return The last error message or null
     */
    fun getLastError(): String? {
        return errors?.lastOrNull()
    }

    /**
     * Returns the error at the specified index in the errors list.
     * If the index is out of bounds or there are no errors, returns null.
     *
     * @param index The index of the error to retrieve
     * @return The error message at the specified index or null
     */
    fun getErrorAt(index: Int): String? {
        return errors?.getOrNull(index)
    }

    /**
     * Returns a primary error message, prioritizing in this order:
     * 1. The first item in the errors list
     * 2. The message field
     * 3. A default message
     *
     * @param defaultMessage The default message to use if no errors are found
     * @return The primary error message
     */
    fun getPrimaryError(defaultMessage: String = "An error occurred"): String {
        return getFirstError() ?: message.takeIf { it.isNotBlank() } ?: defaultMessage
    }

    /**
     * Indicates whether there are any error messages in the errors list.
     *
     * @return true if there are errors, false otherwise
     */
    fun hasErrors(): Boolean {
        return !errors.isNullOrEmpty()
    }

    /**
     * Returns the number of errors in the errors list.
     *
     * @return The number of errors, or 0 if there are none
     */
    fun getErrorCount(): Int {
        return errors?.size ?: 0
    }

    /**
     * Maps each error using the provided transformation function.
     *
     * @param transform A function to transform each error string
     * @return A list of transformed errors, or null if there are no errors
     */
    fun <R> mapErrors(transform: (String) -> R): List<R>? {
        return errors?.map(transform)
    }

    /**
     * Returns all error information combined into a single descriptive string.
     * Includes error code if available.
     *
     * @return A comprehensive error description string
     */
    fun getDetailedErrorInfo(): String {
        val sb = StringBuilder()

        if (!message.isBlank()) {
            sb.append("Message: $message")
        }

        if (errorCode != null) {
            if (sb.isNotEmpty()) sb.append("\n")
            sb.append("Error Code: $errorCode")
        }

        if (!errors.isNullOrEmpty()) {
            if (sb.isNotEmpty()) sb.append("\n")
            sb.append("Errors:\n- ${errors!!.joinToString("\n- ")}")
        }

        return sb.toString().ifEmpty { "No error details available" }
    }

    /**
     * Returns the appropriate message based on error status.
     * If error is false (success), returns the message field.
     * If error is true, returns the first error message if available,
     * otherwise falls back to the message field.
     *
     * @param defaultMessage Optional fallback if no message is available
     * @return The appropriate message based on response status
     */
    fun getStatusMessage(defaultMessage: String = ""): String {

        return if (!error) {
            // Success case - use the message field
            defaultMessage.ifBlank { message }
        } else {
            // Error case - prioritize first error from errors list, then message field
            getFirstError() ?: message.ifBlank { defaultMessage }
        }
    }
}