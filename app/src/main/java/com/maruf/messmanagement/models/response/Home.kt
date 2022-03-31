package com.maruf.messmanagement.models.response


import com.google.gson.annotations.SerializedName

data class Home(
    @SerializedName("mealCharge")
    var mealCharge: String = "",
    @SerializedName("totalMeal")
    var totalMeal: String = ""
)