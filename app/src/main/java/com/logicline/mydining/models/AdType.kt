package com.logicline.mydining.models
import com.google.gson.annotations.SerializedName

data class AdType(
    @SerializedName("show") val show: Boolean = false,
    @SerializedName("acc_age") val accAge: Int = 0,
    @SerializedName("interval") val interval: Int = 0,
    @SerializedName("platform") val platform: String = " ",
)
