package com.logicline.mydining.data.models

import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.local.entities.UserDataEntity
import com.logicline.mydining.data.local.entities.UserEntity
import com.logicline.mydining.data.local.relations.UserDataWithRelations

data class UserData(
    @SerializedName("user") var user: User?,
    @SerializedName("mess_user") var messUser: MessUser?,
    @SerializedName("token") var token: String?,
){
    fun toEntity(): UserDataEntity {
        return UserDataEntity(
            userId = user?.id ?: 0,
            token = token,
            messUserId = messUser?.id
        )
    }

    fun toRelation(): UserDataWithRelations {
        return UserDataWithRelations(
            userData = toEntity(),
            user = user?.toEntity(),
            messUser = messUser?.toRelation()
        )
    }
}

