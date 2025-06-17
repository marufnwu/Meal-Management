package com.logicline.mydining.di

import com.logicline.mydining.data.local.dao.MyDao
import com.logicline.mydining.data.repository.MonthRepository
import com.logicline.mydining.data.repository.ProfileRepository
import com.logicline.mydining.data.repository.Repository
import com.logicline.mydining.data.repository.RepositoryImpl
import com.logicline.mydining.network.MyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMonthRepository(myApi: MyApi): MonthRepository {
        return MonthRepository(myApi)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(myApi: MyApi): ProfileRepository {
        return ProfileRepository(myApi)
    }

    @Provides
    @Singleton
    fun provideRepository(
        myApi: MyApi,
        myDao: MyDao
    ): Repository {
        return RepositoryImpl(myApi, myDao)
    }

    // Add other repositories similarly
}