package com.logicline.mydining.data.repository

import com.logicline.mydining.data.local.dao.MessUserDao
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import com.logicline.mydining.network.MyApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessRepository @Inject constructor(
    private val myApi: MyApi,
    private val messUserDao: MessUserDao
) {
    suspend fun createMess(name: String) = myApi.createMess(name)

    suspend fun getMessUser(): Flow<MessUserWithRelations?> = messUserDao.getMessUser()

    suspend fun saveMessUserLocally(messUser: MessUserWithRelations) {
        // extract related entities and save via insertFullMessUser
        messUserDao.insertFullMessUser(
            messUser = messUser.messUser,
            user = messUser.user,
            mess = messUser.mess,
            role = messUser.role,
            permissions = messUser.permissions
        )
    }
}
