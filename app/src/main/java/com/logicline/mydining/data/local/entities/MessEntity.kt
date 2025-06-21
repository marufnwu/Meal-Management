package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.logicline.mydining.data.models.Mess

@Entity(tableName = "mess")
data class MessEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val adFree: Boolean,
    val allUserAddMeal: Boolean,
    val fundAddEnabled: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

fun MessEntity.toDomainModel(): Mess? {
    return Mess(
        id = id,
        name = name,
        status = status,
        adFree = adFree,
        allUserAddMeal = allUserAddMeal,
        fundAddEnabled = fundAddEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
