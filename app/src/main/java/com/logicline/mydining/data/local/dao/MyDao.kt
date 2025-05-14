package com.logicline.mydining.data.local.dao

import android.util.Log
import androidx.room.*
import com.google.gson.Gson
import com.logicline.mydining.data.local.entities.*
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import com.logicline.mydining.data.local.relations.UserDataWithRelations
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.toEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {

    /* --- Queries --- */

    @Transaction
    @Query("SELECT * FROM mess_user ORDER BY id DESC LIMIT 1")
    fun getMessUser(): Flow<MessUserWithRelations?>

    @Transaction
    @Query("SELECT * FROM user_data LIMIT 1")
    fun getUserData(): Flow<UserDataWithRelations?>


    /* --- Inserts --- */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessUser(messUser: MessUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMess(mess: MessEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: RoleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermissions(permissions: List<PermissionEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(userData: UserDataEntity): Long



    /* --- Clears --- */

    @Query("DELETE FROM mess_user")
    suspend fun clearMessUser()

    @Query("DELETE FROM user")
    suspend fun clearUser()

    @Query("DELETE FROM mess")
    suspend fun clearMess()

    @Query("DELETE FROM role")
    suspend fun clearRole()

    @Query("DELETE FROM permissions")
    suspend fun clearPermissions()

    @Query("DELETE FROM user_data")
    suspend fun clearUserData()

    /* --- Clear All --- */

    @Transaction
    suspend fun clearLocalCache() {
        clearUserData()
        clearMessUser()
        clearUser()
        clearMess()
        clearRole()
        clearPermissions()
    }

    /* --- Insert Helpers --- */

    @Transaction
    suspend fun insertMessUserWithRelations(messUserWithRelations: MessUserWithRelations?) {
        clearLocalCache()

        Log.d("insertMessUserWithRelations: ", Gson().toJson(messUserWithRelations))

        messUserWithRelations?.let { relations ->
            relations.user?.let { insertUser(it) }
            relations.mess?.let { insertMess(it) }
            relations.role?.let { insertRole(it) }
            relations.permissions?.takeIf { it.isNotEmpty() }?.let { insertPermissions(it) }
            relations.messUser.let { insertMessUser(it) }
        }
    }

    @Transaction
    suspend fun saveUserData(userDataWithRelations: UserDataWithRelations?) {
        clearLocalCache()

        userDataWithRelations?.let { relations ->
            insertUserData(userDataWithRelations.userData)
            relations.user?.let { insertUser(it) }
            relations.messUser?.let { messUser ->
                messUser.user?.let { insertUser(it) }
                messUser.mess?.let { insertMess(it) }
                messUser.role?.let { insertRole(it) }
                messUser.permissions?.takeIf { it.isNotEmpty() }?.let { insertPermissions(it) }
                insertMessUser(messUser.messUser)
            }
        }
    }
}
