package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.logicline.mydining.data.models.Role

@Entity(tableName = "role")
data class RoleEntity(
    @PrimaryKey val id: Long,
    val messId: Long,
    val role: String,
    val isDefault: Boolean,
    val isAdmin: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

// For RoleEntity
fun RoleEntity.toDomainModel(): Role {
    return Role(
        id = id,
        messId = messId,
        role = role,
        isDefault = isDefault,
        isAdmin = isAdmin,
        createdAt = createdAt,
        updatedAt = updatedAt,
        permissions = null // Permissions will be set separately
    )
}
