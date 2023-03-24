package com.logicline.mydining.models.response

import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.Meal

data class UserDayMealResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("meal")
    var meal: Meal? = null
)