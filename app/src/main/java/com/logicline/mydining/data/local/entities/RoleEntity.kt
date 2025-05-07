package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

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