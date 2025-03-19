package com.logicline.mydining.models
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class MessUser(
    @SerializedName("id") val id: Int,
    @SerializedName("mess_id") val messId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("mess_role_id") val messRoleId: Int,
    @SerializedName("joined_at") val joinedAt: LocalDateTime,
    @SerializedName("left_at") val leftAt: LocalDateTime?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("model_name") val modelName: String,
    @SerializedName("user") val user: User?,
    @SerializedName("mess") val mess: Mess?,
    @SerializedName("role") val role: Role?
)



