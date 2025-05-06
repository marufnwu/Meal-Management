package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "permissions")
data class PermissionEntity(
    @PrimaryKey val id: Long,
    val messRoleId: Long,
    val permission: String,
    val createdAt: String?,
    val updatedAt: String?
)
