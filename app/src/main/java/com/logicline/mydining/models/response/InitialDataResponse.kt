package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName

data class InitialDataResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("initialData")
    var initialData: InitialData? = null,
    @SerializedName("msg")
    var msg: String = "", )