package com.logicline.mydining.utils

import com.logicline.mydining.models.Ad
import com.logicline.mydining.models.User
import com.logicline.mydining.models.response.InitialData
import io.paperdb.Paper

object LocalDB {
    private const val KEY_USER = "user"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_USER_ID = "user_id"
    private const val FIRST_OPEN_ID = "first_open"

    private const val KEY_AD_SETTINGS = "ad_settings"
    private const val KEY_INITIAL_DATA = "KEY_INITIAL_DATA"


    fun saveInitialData(initialData: InitialData){
        Paper.book().write(KEY_INITIAL_DATA, initialData)
    }

    fun getInitialData():InitialData?{
        return Paper.book().read(KEY_INITIAL_DATA)
    }

    fun saveAdSettings(ad:Ad){
        Paper.book().write(KEY_AD_SETTINGS, ad)
    }

    fun getAdSettings(): Ad?{
        return Paper.book().read(KEY_AD_SETTINGS)
    }

    fun saveUser(user: User){
        Paper.book().write(KEY_USER, user)
    }

    fun getUser(): User?{
        return Paper.book().read<User>(KEY_USER)
    }

    fun saveAccessToken(token:String){
        Paper.book().write(KEY_TOKEN, token)
    }

    fun getAccessToken():String?{
        return Paper.book().read<String>(KEY_TOKEN)
    }

    fun saveUserId(userId:Int){
        Paper.book().write(KEY_USER_ID, userId)
    }

    fun getUserId():Int?{
        return Paper.book().read<Int>(KEY_USER_ID)
    }

    fun logout(){
        Paper.book().delete(KEY_USER)
        Paper.book().delete(KEY_USER_ID)
        Paper.book().delete(KEY_TOKEN)
    }

    fun isFirstOpen(): Boolean {
        return Paper.book().read(FIRST_OPEN_ID) ?: return true

    }

    fun setFirstOpen(v:Boolean){
        Paper.book().write(FIRST_OPEN_ID, v)
    }


}