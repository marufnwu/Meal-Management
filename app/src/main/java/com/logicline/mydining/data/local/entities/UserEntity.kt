package com.logicline.mydining.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.logicline.mydining.data.models.User

@Entity(tableName = "user")
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

fun UserEntity.toDomainModel() : User?{
    return User(
        id = id,
        name = name,
        userName = userName,
        email = email,
        emailVerifiedAt = emailVerifiedAt,
        countryId = countryId,
        phone = phone,
        gender = gender,
        city = city,
        status = status,
        joinDate = joinDate,
        leaveDate = leaveDate,
        photoUrl = photoUrl,
        fcmToken = fcmToken,
        version = version,
        lastActive = lastActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
        country = null
    )
}



