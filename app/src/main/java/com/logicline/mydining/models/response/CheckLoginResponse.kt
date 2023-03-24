package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.User

data class CheckLoginResponse(
    @SerializedName("token")
    var token: String = "",
    @SerializedName("userId")
    var userId: Int = 0,
    @SerializedName("user")
    var user: User? = null
)