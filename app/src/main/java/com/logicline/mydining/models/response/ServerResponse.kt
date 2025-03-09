package com.logicline.mydining.models.response

import com.google.gson.annotations.SerializedName

class ServerResponse<T> (
    @SerializedName("error") var error: Boolean = true,
    @SerializedName("message") var msg: String = "",
    @SerializedName("data") var data: T? = null,
 )