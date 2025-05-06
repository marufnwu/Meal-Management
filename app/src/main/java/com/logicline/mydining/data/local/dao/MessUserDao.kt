package com.logicline.mydining.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.logicline.mydining.data.local.entities.MessEntity
import com.logicline.mydining.data.local.entities.MessUserEntity
import com.logicline.mydining.data.local.entities.PermissionEntity
import com.logicline.mydining.data.local.entities.RoleEntity
import com.logicline.mydining.data.local.entities.UserEntity
import com.logicline.mydining.data.local.relations.MessUserWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface MessUserDao {
    @Transaction
    @Query("SELECT * FROM mess_user LIMIT 1")
    fun getMessUser(): Flow<MessUserWithRelations?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessUser(messUser: MessUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMess(mess: MessEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: RoleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermissions(permissions: List<PermissionEntity>)

    @Transaction
    suspend fun insertFullMessUser(
        messUser: MessUserEntity,
        user: UserEntity?,
        mess: MessEntity?,
        role: RoleEntity?,
        permissions: List<PermissionEntity>?
    ) {
        insertMessUser(messUser)
        user?.let {
            insertUser(user)

        }
        user?.let {
            insertUser(it)
        }

        mess?.let {
            insertMess(it)
        }

        role?.let {
            insertRole(it)
        }

        permissions?.takeIf { it.isNotEmpty() }?.let {
            insertPermissions(it)
        }
    }
}

