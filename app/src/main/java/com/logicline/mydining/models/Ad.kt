package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

data class Ad(
    @SerializedName("show") val show: Boolean = false,
    @SerializedName("fullScreen") val fullScreen: AdType = AdType(),
    @SerializedName("native") val native: AdType = AdType(),
    @SerializedName("banner") val banner: AdType = AdType(),

 )
