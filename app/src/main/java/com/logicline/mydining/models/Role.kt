package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Role(
    @SerializedName("id") val id: Long,
    @SerializedName("mess_id") val messId: Long,
    @SerializedName("role") val role: String,
    @SerializedName("is_default") val isDefault: Boolean,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("permissions") val permissions: List<Permission>?
)
