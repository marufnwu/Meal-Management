package com.logicline.mydining.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.logicline.mydining.data.local.entities.MessEntity
import com.logicline.mydining.data.local.entities.MessUserEntity
import com.logicline.mydining.data.local.entities.PermissionEntity
import com.logicline.mydining.data.local.entities.RoleEntity
import com.logicline.mydining.data.local.entities.UserEntity
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.Permission
import com.logicline.mydining.data.models.Role
import com.logicline.mydining.data.models.User

data class MessUserWithRelations(
    @Embedded val messUser: MessUserEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity? = null,

    @Relation(
        parentColumn = "messId",
        entityColumn = "id"
    )
    val mess: MessEntity? = null,

    @Relation(
        parentColumn = "messRoleId",
        entityColumn = "id"
    )
    val role: RoleEntity? = null,


    @Relation(
        parentColumn = "messRoleId",
        entityColumn = "messRoleId"
    )
    val permissions: List<PermissionEntity>? = null
)

fun MessUserWithRelations.toDomainModel(): MessUser {
    return MessUser(
        id = messUser.id,
        messId = messUser.messId,
        userId = messUser.userId,
        messRoleId = messUser.messRoleId,
        joinedAt = messUser.joinedAt,
        leftAt = messUser.leftAt,
        status = messUser.status,
        createdAt = messUser.createdAt,
        updatedAt = messUser.updatedAt,
        user = user?.let {
            User(
                id = it.id,
                name = it.name,
                userName = it.userName,
                email = it.email,
                emailVerifiedAt = it.emailVerifiedAt,
                countryId = it.countryId,
                phone = it.phone,
                gender = it.gender,
                city = it.city,
                status = it.status,
                joinDate = it.joinDate,
                leaveDate = it.leaveDate,
                photoUrl = it.photoUrl,
                fcmToken = it.fcmToken,
                version = it.version,
                lastActive = it.lastActive,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                country = null
            )
        },
        mess = mess?.let {
            Mess(
                id = it.id,
                name = it.name,
                status = it.status,
                adFree = it.adFree,
                allUserAddMeal = it.allUserAddMeal,
                fundAddEnabled = it.fundAddEnabled,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        },
        role = role?.let {
            Role(
                id = it.id,
                messId = it.messId,
                role = it.role,
                isDefault = it.isDefault,
                isAdmin = it.isAdmin,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                permissions = permissions?.map { perm ->
                    Permission(
                        id = perm.id,
                        messRoleId = perm.messRoleId,
                        permission = perm.permission,
                        createdAt = perm.createdAt,
                        updatedAt = perm.updatedAt
                    )
                }
            )
        },
        modelName = null
    )
}
