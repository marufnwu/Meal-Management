package com.logicline.mydining.data.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("profile_completion")
    val profileCompletion: Int,
    @SerializedName("last_updated")
    val lastUpdated: String
) : Parcelable

@Parcelize
data class ProfileUpdateRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("gender")
    val gender: String? = null
) : Parcelable

@Parcelize
data class AvatarUploadResponse(
    @SerializedName("photo_url")
    val photoUrl: String,
    @SerializedName("message")
    val message: String
) : Parcelable
