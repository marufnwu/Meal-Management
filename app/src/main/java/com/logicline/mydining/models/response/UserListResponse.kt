package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.User

data class UserListResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("userList")
    var userList: List<User>? = null
)