package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

data class Support(
    @SerializedName("active") var active: Boolean = false,
    @SerializedName("type") var type: String = "",
    @SerializedName("action") var action: String = "",
)