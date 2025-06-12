package com.logicline.mydining.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Common base structure for both month and user summaries
 */
open class BaseSummary(
    @SerializedName("month") open val month: Month,
    @SerializedName("summary") open val summary: SummaryData,
    @SerializedName("details") open val details: Any?
) : Serializable

/**
 * Month summary (minimal or detailed)
 */
data class MonthSummary(
    @SerializedName("month") val month: Month,
    @SerializedName("summary") val summary: SummaryData,
    @SerializedName("details") val details: MonthDetails?
) :  Serializable

/**
 * User summary (minimal or detailed)
 */
data class UserSummary(
    @SerializedName("month") val month: Month,
    @SerializedName("mess_user") val messUser: MessUser,
    @SerializedName("summary") val summary: SummaryData,
    @SerializedName("details") val details: UserDetails?,
    @SerializedName("month_overview") val monthOverview: MonthOverview?
) :  Serializable


/**
 * Common summary data structure
 * Notes:
 * - For month summary, some fields may be null
 * - For user summary, all fields should be populated
 */
data class SummaryData(
    @SerializedName("total_meal") val totalMeal: Float,
    @SerializedName("total_deposit") val totalDeposit: Float? = null,
    @SerializedName("deposit") val deposit: Float? = null,
    @SerializedName("total_purchase") val totalPurchase: Float? = null,
    @SerializedName("total_other_cost") val totalOtherCost: Float? = null,
    @SerializedName("total_cost") val totalCost: Float,
    @SerializedName("meal_charge") val mealCharge: Float? = null,
    @SerializedName("other_cost_share") val otherCostShare: Float? = null,
    @SerializedName("meal_rate") val mealRate: Float,
    @SerializedName("balance") val balance: Float? = null,
    @SerializedName("due") val due: Float? = null,
    @SerializedName("status") val status: String
) : Serializable

/**
 * Month details for detailed response
 */
data class MonthDetails(
    @SerializedName("meal_summary") val mealSummary: MealSummary,
    @SerializedName("users") val users: List<UserSummaryItem>
) : Serializable

/**
 * User details for detailed response
 */
data class UserDetails(
    @SerializedName("meal_summary") val mealSummary: UserMealSummary,
    @SerializedName("financial_details") val financialDetails: FinancialDetails,
    @SerializedName("history") val history: HistoryData
) : Serializable

/**
 * Meal summary for month
 */
data class MealSummary(
    @SerializedName("breakfast") val breakfast: Int,
    @SerializedName("lunch") val lunch: Int,
    @SerializedName("dinner") val dinner: Int
) : Serializable

/**
 * Meal summary for user
 */
data class UserMealSummary(
    @SerializedName("breakfast") val breakfast: Float,
    @SerializedName("lunch") val lunch: Float,
    @SerializedName("dinner") val dinner: Float,
    @SerializedName("percentage") val percentage: Float
) : Serializable

/**
 * Financial details for user
 */
data class FinancialDetails(
    @SerializedName("deposit_percentage") val depositPercentage: Float,
    @SerializedName("purchases") val purchases: PurchaseData,
    @SerializedName("other_costs") val otherCosts: OtherCostData,
    @SerializedName("contribution") val contribution: Float
) : Serializable

/**
 * History data for user
 */
data class HistoryData(
    @SerializedName("meals") val meals: List<Meal>,
    @SerializedName("deposits") val deposits: List<Deposit>
) : Serializable

/**
 * User summary item for month detail
 */
data class UserSummaryItem(
    @SerializedName("mess_user") val messUser: MessUser,
    @SerializedName("meals") val meals: MealSummaryItem,
    @SerializedName("deposit") val deposit: Float,
    @SerializedName("meal_charge") val mealCharge: Float,
    @SerializedName("other_cost_share") val otherCostShare: Float,
    @SerializedName("total_cost") val totalCost: Float,
    @SerializedName("balance") val balance: Float,
    @SerializedName("due") val due: Float,
    @SerializedName("status") val status: String
) : Serializable

/**
 * Mess user info
 */
data class MessUserInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int
) : Serializable

/**
 * Meal summary item
 */
data class MealSummaryItem(
    @SerializedName("breakfast") val breakfast: Int,
    @SerializedName("lunch") val lunch: Int,
    @SerializedName("dinner") val dinner: Int,
    @SerializedName("total") val total: Int
) : Serializable

/**
 * Purchase data
 */
data class PurchaseData(
    @SerializedName("total") val total: Float,
    @SerializedName("recent") val recent: List<Purchase>
) : Serializable

/**
 * Other cost data
 */
data class OtherCostData(
    @SerializedName("total") val total: Float,
    @SerializedName("recent") val recent: List<Purchase>
) : Serializable


/**
 * Month overview
 */
data class MonthOverview(
    @SerializedName("total_meals") val totalMeals: Float,
    @SerializedName("total_deposit") val totalDeposit: Float,
    @SerializedName("total_purchase") val totalPurchase: Float,
    @SerializedName("total_other_cost") val totalOtherCost: Float,
    @SerializedName("total_cost") val totalCost: Float
) : Serializable