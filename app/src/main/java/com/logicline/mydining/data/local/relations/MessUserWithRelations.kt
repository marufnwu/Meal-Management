package com.logicline.mydining.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.logicline.mydining.data.local.entities.MessEntity
import com.logicline.mydining.data.local.entities.MessUserEntity
import com.logicline.mydining.data.local.entities.PermissionEntity
import com.logicline.mydining.data.local.entities.RoleEntity
import com.logicline.mydining.data.local.entities.UserEntity
import com.logicline.mydining.data.local.entities.toDomainModel
import com.logicline.mydining.data.models.MessUser

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
        user = user?.toDomainModel(),
        mess = mess?.toDomainModel(),
        role = role?.toDomainModel()?.copy(
            permissions = permissions.toDomainModel()
        ),
        modelName = null
    )
}
