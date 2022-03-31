package com.maruf.messmanagement.models.response


import com.google.gson.annotations.SerializedName

data class HomeDataResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("home")
    var home: Home? = null,
    @SerializedName("msg")
    var msg: String = ""
)