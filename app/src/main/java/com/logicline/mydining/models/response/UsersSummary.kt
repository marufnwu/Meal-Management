package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.User

data class UsersSummary(
    @SerializedName("deposit")
    var deposit: String = "00",
    @SerializedName("due")
    var due: String = "00",
    @SerializedName("meal")
    var meal: String = "00",
    @SerializedName("mealCost")
    var mealCost: String = "00",
    @SerializedName("otherCost")
    var otherCost: String = "00",
    @SerializedName("totalCost")
    var totalCost: String = "00",
    @SerializedName("user")
    var user: User? = null
)