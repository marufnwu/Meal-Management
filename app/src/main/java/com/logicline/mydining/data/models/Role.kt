package com.logicline.mydining.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    @SerializedName("id") val id: Long,
    @SerializedName("mess_id") val messId: Long,
    @SerializedName("role") val role: String,
    @SerializedName("is_default") val isDefault: Boolean,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("permissions") val permissions: List<Permission>?
) : Parcelable
