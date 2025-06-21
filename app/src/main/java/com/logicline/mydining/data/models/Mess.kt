package com.logicline.mydining.data.models

import android.R
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.local.entities.MessEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mess(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("ad_free") val adFree: Boolean,
    @SerializedName("all_user_add_meal") val allUserAddMeal: Boolean,
    @SerializedName("fund_add_enabled") val fundAddEnabled: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("is_accepting_members") val isAcceptingMembers: Boolean = true
) : Parcelable


fun Mess.toEntity(): MessEntity {
    return MessEntity(
        id = id,
        name = name,
        status = status,
        adFree = adFree,
        allUserAddMeal = allUserAddMeal,
        fundAddEnabled = fundAddEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}