package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

import android.os.Parcelable

data class UserData(
    @SerializedName("user") var user: User?,
    @SerializedName("mess_user") var messUser: MessUser?,
    @SerializedName("token") var token: String?,
)