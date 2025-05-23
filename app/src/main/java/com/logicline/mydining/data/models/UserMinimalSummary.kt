package com.logicline.mydining.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserMinimalSummary(
    @SerializedName("mess_user") val messUser: MessUser,
    @SerializedName("month_id") val monthId: Int,
    @SerializedName("total_meal") val totalMeal: Float,
    @SerializedName("deposit") val deposit: Float,
    @SerializedName("meal_charge") val mealCharge: Float,
    @SerializedName("other_cost_share") val otherCostShare: Float,
    @SerializedName("total_cost") val totalCost: Float,
    @SerializedName("balance") val balance: Float,
    @SerializedName("due") val due: Float,
    @SerializedName("status") val status: String,
    @SerializedName("meal_rate") val mealRate: Float
) : Serializable
