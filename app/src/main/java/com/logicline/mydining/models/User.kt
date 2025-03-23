package com.logicline.mydining.models

import android.os.Parcelable
import java.sql.Timestamp
import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Define enums to match Laravel's Gender and AccountStatus enums
enum class Gender {
    MALE, FEMALE, OTHER
}

enum class AccountStatus(val value: String) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    BANNED("banned")
}
data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("user_name")
    val userName: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: Timestamp?,
    @SerializedName("country_id")
    val countryId: Int?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("gender")
    val gender: Gender,
    @SerializedName("city")
    val city: String?,
    @SerializedName("password")
    val password: String,
    @SerializedName("remember_token")
    val rememberToken: String?,
    @SerializedName("status")
    val status: AccountStatus = AccountStatus.ACTIVE,
    @SerializedName("join_date")
    val joinDate: Timestamp?,
    @SerializedName("leave_date")
    val leaveDate: Timestamp?,
    @SerializedName("photo_url")
    val photoUrl: String?,
    @SerializedName("fcm_token")
    val fcmToken: String?,
    @SerializedName("version")
    val version: Int = 0,
    @SerializedName("last_active")
    val lastActive: Timestamp?,
    @SerializedName("created_at")
    val createdAt: Timestamp?,
    @SerializedName("updated_at")
    val updatedAt: Timestamp?
) : Serializable