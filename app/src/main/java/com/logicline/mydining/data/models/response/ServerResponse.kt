package com.logicline.mydining.data.models.response

import com.google.gson.annotations.SerializedName

class ServerResponse<T> (
    @SerializedName("error") var error: Boolean = true,
    @SerializedName("message") var msg: String = "",
    @SerializedName("data") var data: T? = null,
    @SerializedName("errors") var errors: Any? = null,
    @SerializedName("error_code") var errorCode: String? = null
) {
    val success: Boolean
        get() = !error
    
    val message: String
        get() = msg
}