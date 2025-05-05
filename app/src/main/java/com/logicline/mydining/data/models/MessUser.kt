package com.logicline.mydining.data.models
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessUser(
    @SerializedName("id") val id: Int,
    @SerializedName("mess_id") val messId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("mess_role_id") val messRoleId: Int,
    @SerializedName("joined_at") val joinedAt: String?,
    @SerializedName("left_at") val leftAt: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("model_name") val modelName: String?,
    @SerializedName("user") val user: User?,
    @SerializedName("mess") val mess: Mess?,
    @SerializedName("role") val role: Role?
) : Parcelable



