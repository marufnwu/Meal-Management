package com.maruf.messmanagement.models.response

import com.google.gson.annotations.SerializedName
import com.maruf.messmanagement.models.Meal

data class UserDayMealResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("meal")
    var meal: Meal? = null
)