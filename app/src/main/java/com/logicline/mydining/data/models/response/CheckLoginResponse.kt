package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.User

data class CheckLoginResponse(
    @SerializedName("token")
    var token: String = "",
    @SerializedName("user_id")
    var userId: Int = 0,
    @SerializedName("user")
    var user: User? = null
)