package com.logicline.mydining.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.local.entities.MessEntity
import com.logicline.mydining.data.local.entities.MessUserEntity
import com.logicline.mydining.data.local.entities.PermissionEntity
import com.logicline.mydining.data.local.entities.RoleEntity
import com.logicline.mydining.data.local.entities.UserEntity
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import com.logicline.mydining.data.local.relations.RoleWithPermissions
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
    @SerializedName("role") val role: Role?,
    @SerializedName("is_user_left_mess") val isUserLeftMess: Boolean,

) : Parcelable


fun MessUser.toEntity(): MessUserEntity {
    return MessUserEntity(
        id = id,
        messId = messId,
        userId = userId,
        messRoleId = messRoleId,
        joinedAt = joinedAt,
        leftAt = leftAt,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isUserLeftMess = isUserLeftMess
    )
}

fun MessUser.toRelation(): MessUserWithRelations {

    return MessUserWithRelations(
        messUser = toEntity(),
        mess = mess?.toEntity(),
        user = user?.toEntity(),
        role = role?.toEntity(),
        permissions = role?.permissions?.toEntity()
    )
}


