package com.logicline.mydining.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.logicline.mydining.data.local.dao.MessUserDao
import com.logicline.mydining.data.local.entities.MessEntity
import com.logicline.mydining.data.local.entities.MessUserEntity
import com.logicline.mydining.data.local.entities.PermissionEntity
import com.logicline.mydining.data.local.entities.RoleEntity
import com.logicline.mydining.data.local.entities.UserEntity

@Database(
    entities = [
        MessUserEntity::class,
        UserEntity::class,
        MessEntity::class,
        RoleEntity::class,
        PermissionEntity::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messUserDao(): MessUserDao
}
