package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.Mess
import com.logicline.mydining.models.Month
import com.logicline.mydining.models.Support

data class InitialData(
    @SerializedName("mealCharge")
    var mealCharge: String = "",
    @SerializedName("totalMeal")
    var totalMeal: String = "",
    @SerializedName("messName")
    var messName: String = "",
    @SerializedName("support")
    var support: Support? = null,
    @SerializedName("messData")
    var messData: Mess? = null,
    @SerializedName("runningMonth")
    var month: Month? = null
)