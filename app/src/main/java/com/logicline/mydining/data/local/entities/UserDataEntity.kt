package com.logicline.mydining.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.logicline.mydining.data.local.relations.UserDataWithRelations

@Entity(tableName = "user_data")
data class UserDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id") val userId: Int?,
    val token: String?,
    @ColumnInfo(name = "mess_user_id") val messUserId: Int?
)

