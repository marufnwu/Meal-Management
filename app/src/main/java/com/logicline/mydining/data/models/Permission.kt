package com.logicline.mydining.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.local.entities.PermissionEntity
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

fun Permission.toEntity(): PermissionEntity {
    return PermissionEntity(
        id = id,
        messRoleId = messRoleId,
        permission = permission,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<Permission>.toEntity(): List<PermissionEntity> {
    return map {
        it.toEntity()
    }
}