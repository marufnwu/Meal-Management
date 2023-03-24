package com.logicline.mydining.models.response

import com.google.gson.annotations.SerializedName

class Paging<T>(
    @SerializedName("currPage") val currPage:Int,
    @SerializedName("totalPage") val totalPage:Int,
    @SerializedName("data") val data:MutableList<T>?,
)