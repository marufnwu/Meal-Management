package com.logicline.mydining.di

import android.content.Context
import androidx.room.Room
import com.logicline.mydining.data.local.dao.MyDao
import com.logicline.mydining.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_dining_db"
        ).
        fallbackToDestructiveMigration().
        build()
    }

    @Provides
    fun provideMessUserDao(appDatabase: AppDatabase): MyDao {
        return appDatabase.messUserDao()
    }
}