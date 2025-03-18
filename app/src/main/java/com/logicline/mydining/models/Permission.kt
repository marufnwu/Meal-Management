package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Permission(
    @SerializedName("id") val id: Long,
    @SerializedName("mess_role_id") val messRoleId: Long,
    @SerializedName("permission") val permission: String,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    @SerializedName("updated_at") val updatedAt: LocalDateTime
)