package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.Meal

data class MealListResponse(
    @SerializedName("error")
    var error: Boolean? = false,
    @SerializedName("meals")
    var meals: MutableList<MutableList<Meal>>? = mutableListOf(),
    @SerializedName("month")
    var month: Int? = 0,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("totalMeal")
    var totalMeal: Float? = 0f,
    @SerializedName("year")
    var year: String? = ""
)