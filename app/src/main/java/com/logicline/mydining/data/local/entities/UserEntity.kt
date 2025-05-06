package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val userName: String?,
    val email: String,
    val emailVerifiedAt: String?,
    val countryId: Int?,
    val phone: String?,
    val gender: String,
    val city: String?,
    val password: String,
    val rememberToken: String?,
    val status: String,
    val joinDate: String?,
    val leaveDate: String?,
    val photoUrl: String?,
    val fcmToken: String?,
    val version: Int,
    val lastActive: String?,
    val createdAt: String?,
    val updatedAt: String?
)
