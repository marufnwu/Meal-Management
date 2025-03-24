package com.logicline.mydining.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Permission(
    @SerializedName("id") val id: Long,
    @SerializedName("mess_role_id") val messRoleId: Long,
    @SerializedName("permission") val permission: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
) : Parcelable