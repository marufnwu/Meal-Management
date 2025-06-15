package com.logicline.mydining.data.repository

import android.util.Log
import com.google.gson.Gson
import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.local.dao.MyDao
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import com.logicline.mydining.data.local.relations.UserDataWithRelations
import com.logicline.mydining.data.local.relations.toDomainModel
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.UserSummary
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.extensions.map
import com.logicline.mydining.extensions.safeApiCall
import com.logicline.mydining.network.MyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val myApi: MyApi,
    private val myDao: MyDao
) : Repository {
    override suspend fun createMess(name: String) = myApi.createMess(name)

    override suspend fun observeMessUser() = myDao.getMessUser()

    override suspend fun saveMessUserLocally(messUser: MessUserWithRelations?) {
        Log.d("insertMessUserWithRelations: saveMessUserLocally", Gson().toJson(messUser))

        myDao.insertMessUserWithRelations(
            messUser
        )
    }

    override suspend fun login(userName: String, password: String) = myApi.login(userName, password)
    override suspend fun checkLogin(): Response<ServerResponse<UserData>> = myApi.checkLogin()

    override suspend fun getMessUser() = myApi.messUser()
    override suspend fun saveUserDataLocally(userData: UserDataWithRelations?) {
        myDao.saveUserData(userData)

    }

    override fun observedUserData() = flow {
        emit(DataState.Loading())
        try {
            myDao.getUserData().collect { userWithRelations ->
                emit(DataState.Success(userWithRelations?.toDomainModel()))
            }
        } catch (e: Exception) {
            emit(DataState.Exception(exception = e, message = e.localizedMessage))
        }
    }

    override suspend fun userMinimalMonthSummary(): DataState<UserSummary?>{
        return  safeApiCall {
            myApi.userMinimalMonthSummary()
        }
    }

    override suspend fun userDetailsMonthSummary(): DataState<UserSummary?> {
        TODO("Not yet implemented")
    }
}