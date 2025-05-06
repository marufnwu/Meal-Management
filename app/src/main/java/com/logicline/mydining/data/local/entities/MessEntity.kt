package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mess")
data class MessEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val status: String,
    val adFree: Boolean,
    val allUserAddMeal: Boolean,
    val fundAddEnabled: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)
