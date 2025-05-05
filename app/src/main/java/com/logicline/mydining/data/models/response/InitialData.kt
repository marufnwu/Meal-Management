package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.Support

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
    var messData: Mess? = null
)