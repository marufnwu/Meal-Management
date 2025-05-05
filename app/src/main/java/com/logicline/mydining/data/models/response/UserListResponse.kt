package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.User

data class UserListResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("userList")
    var userList: List<User>? = null
)