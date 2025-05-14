package com.logicline.mydining.data.repository

import android.util.Log
import com.google.gson.Gson
import com.logicline.mydining.data.local.dao.MyDao
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import com.logicline.mydining.data.local.relations.UserDataWithRelations
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.network.MyApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val myApi: MyApi,
    private val myDao: MyDao
)  : Repository {
    override suspend fun createMess(name: String) = myApi.createMess(name)

    override suspend fun observeMessUser() = myDao.getMessUser()

    override suspend fun saveMessUserLocally(messUser: MessUserWithRelations?) {
        Log.d("insertMessUserWithRelations: saveMessUserLocally", Gson().toJson(messUser))

        myDao.insertMessUserWithRelations(
           messUser
        )
    }

    override suspend fun login(userName: String, password: String) =  myApi.login(userName, password)
    override suspend fun checkLogin(): Response<ServerResponse<UserData>> = myApi.checkLogin()

    override suspend fun getMessUser() = myApi.messUser()
    override suspend fun saveUserDataLocally(userData: UserDataWithRelations?) {
        myDao.saveUserData(userData)

    }
    override  fun observedUserData() = myDao.getUserData()
}