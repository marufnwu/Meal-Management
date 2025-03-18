package com.logicline.mydining.models


import com.google.gson.annotations.SerializedName


data class MealsData(
    @SerializedName("meals_by_date") val mealsByDate: MutableList<MealDate>?,
    @SerializedName("overall_totals") val overallTotals: OverallTotals
)

data class Meal(
    @SerializedName("id")
    val id: Int,

    @SerializedName("month_id")
    val monthId: Int,

    @SerializedName("mess_user_id")
    val messUserId: Int,

    @SerializedName("mess_id")
    val messId: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("breakfast")
    val breakfast: Int,

    @SerializedName("lunch")
    val lunch: Int,

    @SerializedName("dinner")
    val dinner: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("model_name")
    val modelName: String
)


data class MealDate(
    @SerializedName("date") val date: String,
    @SerializedName("meals") val meals: MutableList<Meal>,
    @SerializedName("total_meals") val totalMeals: TotalMeals
)


data class TotalMeals(
    @SerializedName("total_breakfast") val totalBreakfast: Int,
    @SerializedName("total_lunch") val totalLunch: Int,
    @SerializedName("total_dinner") val totalDinner: Int
)

data class OverallTotals(
    @SerializedName("total_breakfast") val totalBreakfast: Int,
    @SerializedName("total_lunch") val totalLunch: Int,
    @SerializedName("total_dinner") val totalDinner: Int,
    @SerializedName("total_meals") val totalMeals: Int
)