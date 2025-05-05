package com.logicline.mydining.data.models

import com.google.gson.annotations.SerializedName

data class DepositSum(
    @SerializedName("mess_user_id") val messUserId: Int,
    @SerializedName("latest_date") val latestDate: String,
    @SerializedName("total_amount") val totalAmount: Int,
    @SerializedName("model_name") val modelName: String,
    @SerializedName("mess_user") val messUser: MessUser
)
