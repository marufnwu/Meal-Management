package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

data class OtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otpId") val otpId: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("userName") val userName: String
)