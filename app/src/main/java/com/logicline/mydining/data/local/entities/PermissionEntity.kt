package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.logicline.mydining.data.models.Permission

@Entity(tableName = "permissions")
data class PermissionEntity(
    @PrimaryKey val id: Long,
    val messRoleId: Long,
    val permission: String,
    val createdAt: String?,
    val updatedAt: String?
)
// For PermissionEntity
fun PermissionEntity.toDomainModel(): Permission {
    return Permission(
        id = id,
        messRoleId = messRoleId,
        permission = permission,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// For List<PermissionEntity>
fun List<PermissionEntity>?.toDomainModel(): List<Permission>? {
    return this?.map { it.toDomainModel() }
}