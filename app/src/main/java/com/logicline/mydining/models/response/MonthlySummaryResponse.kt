package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName

data class MonthlySummaryResponse(
    @SerializedName("inReserve")
    var inReserve: String = "00",
    @SerializedName("mealCharge")
    var mealCharge: String = "00",
    @SerializedName("totalCost")
    var totalCost: String = "00",
    @SerializedName("totalDeposit")
    var totalDeposit: String = "00",
    @SerializedName("totalMeal")
    var totalMeal: String = "00",
    @SerializedName("totalOtherCost")
    var totalOtherCost: String = "00",
    @SerializedName("totalMealCost")
    var totalMealCost: String = "00",
    @SerializedName("totalPurchase")
    var totalPurchase: String = "00",
    @SerializedName("totalFund")
    var totalFund: String = "00",
    @SerializedName("usersSummary")
    var usersSummary: List<UsersSummary> = listOf()
)