package com.logicline.mydining.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// Define enums to match Laravel's Gender and AccountStatus enums
enum class Gender {
    MALE, FEMALE, OTHER
}

enum class AccountStatus(val value: String) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    BANNED("banned")
}
@Parcelize
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
    val emailVerifiedAt: String?,
    @SerializedName("country_id")
    val countryId: Int?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("city")
    val city: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("join_date")
    val joinDate: String?,
    @SerializedName("leave_date")
    val leaveDate: String?,
    @SerializedName("photo_url")
    var photoUrl: String?,
    @SerializedName("fcm_token")
    val fcmToken: String?,
    @SerializedName("version")
    val version: Int = 0,
    @SerializedName("last_active")
    val lastActive: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("country")
    val country: Country? = null,
) : Parcelable