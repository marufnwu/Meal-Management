package com.logicline.mydining.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.logicline.mydining.data.local.entities.MessUserEntity
import com.logicline.mydining.data.local.entities.UserDataEntity
import com.logicline.mydining.data.local.entities.UserEntity
import com.logicline.mydining.data.local.entities.toDomainModel
import com.logicline.mydining.data.models.UserData

data class UserDataWithRelations(
    @Embedded val userData: UserDataEntity,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: UserEntity?,
    @Relation(
        parentColumn = "mess_user_id",
        entityColumn = "id",
        entity = MessUserEntity::class
    )
    val messUser: MessUserWithRelations?
)

fun UserDataWithRelations.toDomainModel(): UserData {
    return UserData(
        user = user?.toDomainModel(),
        messUser = messUser?.toDomainModel(),
        token = userData.token
    )
}