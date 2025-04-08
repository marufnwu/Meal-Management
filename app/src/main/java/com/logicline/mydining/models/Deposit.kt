package com.logicline.mydining.models


import com.google.gson.annotations.SerializedName

data class Deposit(
    @SerializedName("mess_user_id") val messUserId: Int,
    @SerializedName("date") var date: String,
    @SerializedName("amount") var amount: Float,
    @SerializedName("month_id") val monthId: Int,
    @SerializedName("mess_id") val messId: Int,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("mess_user") val messUser: MessUser?,
    @SerializedName("model_name") val modelName: String,
)