package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName

data class GenericRespose(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String? = ""
)