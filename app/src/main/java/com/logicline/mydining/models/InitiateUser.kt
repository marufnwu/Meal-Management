package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

class InitiateUser {
    @SerializedName("users") val users: MutableList<User>? = null
    @SerializedName("initiatedUsers") var initiatedUsers: MutableList<User>? = null
}