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
    @SerializedName("role") val role: Role?
) : Parcelable

fun MessUser.toRoomModel(): MessUserWithRelations {
    val messUser = MessUserEntity(
        id = id,
        messId = messId,
        userId = userId,
        messRoleId = messRoleId,
        joinedAt = joinedAt,
        leftAt = leftAt,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = true
    )

    val userEntity = if (user != null) {
        UserEntity(
            id = user.id,
            name = user.name,
            userName = user.userName,
            email = user.email,
            emailVerifiedAt = user.emailVerifiedAt,
            countryId = user.countryId,
            phone = user.phone,
            gender = user.gender,
            city = user.city,
            password = user.password,
            rememberToken = user.rememberToken,
            status = user.status,
            joinDate = user.joinDate,
            leaveDate = user.leaveDate,
            photoUrl = user.photoUrl,
            fcmToken = user.fcmToken,
            version = user.version,
            lastActive = user.lastActive,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    } else {
        null
    }
    val messEntity = if (mess != null) {
        MessEntity(
            id = mess.id,
            name = mess.name,
            status = mess.status,
            adFree = mess.adFree,
            allUserAddMeal = mess.allUserAddMeal,
            fundAddEnabled = mess.fundAddEnabled,
            createdAt = mess.createdAt,
            updatedAt = mess.updatedAt
        )
    } else {
        null
    }
    val roleWithPermissions = if (role != null) {
        RoleWithPermissions(
            role = RoleEntity(
                id = role.id,
                messId = role.messId,
                role = role.role,
                isDefault = role.isDefault,
                isAdmin = role.isAdmin,
                createdAt = role.createdAt,
                updatedAt = role.updatedAt
            ),
            permissions = role.permissions.orEmpty().map {
                PermissionEntity(
                    id = it.id,
                    messRoleId = it.messRoleId,
                    permission = it.permission,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        )
    } else {
        null
    }

    return MessUserWithRelations(
        messUser = messUser,
        mess = messEntity,
        user = userEntity,
        role = roleWithPermissions?.role,
        permissions = roleWithPermissions?.permissions
    )
}


