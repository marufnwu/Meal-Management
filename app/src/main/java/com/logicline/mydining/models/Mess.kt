package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

data class Mess(
    @SerializedName("ad_free") val adFree: Int,
    @SerializedName("all_user_add_meal") var allUserAddMeal: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("status") val status: Int,
    @SerializedName("super_user_id") val superUserId: Int,
    @SerializedName("fund") var fundStatus: Int = 0,
    @SerializedName("type") var type : Int = 0
)