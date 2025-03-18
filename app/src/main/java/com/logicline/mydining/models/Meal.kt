package com.logicline.mydining.models


import com.google.gson.annotations.SerializedName

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