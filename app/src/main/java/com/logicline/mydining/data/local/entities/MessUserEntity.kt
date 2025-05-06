package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.logicline.mydining.data.models.MessUser

@Entity(tableName = "mess_user")
data class MessUserEntity(
    @PrimaryKey val id: Int,
    val messId: Int,
    val userId: Int,
    val messRoleId: Int,
    val joinedAt: String?,
    val leftAt: String?,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?,
    val isSynced: Boolean = false
)