package com.logicline.mydining.data.models.response

import com.google.gson.annotations.SerializedName

class ServerResponse<T> (
    @SerializedName("error") var error: Boolean = true,
    @SerializedName("message") var msg: String = "",
    @SerializedName("data") var data: T? = null,
    @SerializedName("errors") var errors: List<String> ? = null,
 )