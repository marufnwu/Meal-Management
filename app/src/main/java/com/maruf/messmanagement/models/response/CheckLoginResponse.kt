package com.maruf.messmanagement.models.response


import com.google.gson.annotations.SerializedName
import com.maruf.messmanagement.models.User

data class CheckLoginResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("user")
    var user: User? = null
)