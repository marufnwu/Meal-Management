package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName
import com.logicline.mydining.enums.MessStatus
import java.time.LocalDateTime

data class Mess(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("ad_free") val adFree: Boolean,
    @SerializedName("all_user_add_meal") val allUserAddMeal: Boolean,
    @SerializedName("fund_add_enabled") val fundAddEnabled: Boolean,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    @SerializedName("updated_at") val updatedAt: LocalDateTime
)
