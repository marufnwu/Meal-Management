package com.logicline.mydining.data.repository

import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.local.dao.MyDao
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import com.logicline.mydining.data.local.relations.UserDataWithRelations
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.UserSummary
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.network.MyApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    suspend fun createMess(name: String): Response<ServerResponse<MessUser>>
    suspend fun observeMessUser(): Flow<MessUserWithRelations?>
    suspend fun saveMessUserLocally(messUser: MessUserWithRelations?)
    suspend fun login(userName: String, password: String): Response<ServerResponse<UserData>>
    suspend fun checkLogin(): Response<ServerResponse<UserData>>
    suspend fun getMessUser() : Response<ServerResponse<MessUser>>
    suspend fun saveUserDataLocally(userData: UserDataWithRelations?)
    fun observedUserData() : Flow<DataState<UserData?>>
    suspend fun userMinimalMonthSummary() : DataState<UserSummary?>
    suspend fun userDetailsMonthSummary() : DataState<UserSummary?>


}