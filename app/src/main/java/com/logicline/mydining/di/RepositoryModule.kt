package com.logicline.mydining.di

import com.logicline.mydining.data.local.dao.MessUserDao
import com.logicline.mydining.data.repository.MessRepository
import com.logicline.mydining.data.repository.MonthRepository
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
    fun provideMessRepository(
        myApi: MyApi,
        messUserDao: MessUserDao
    ): MessRepository {
        return MessRepository(myApi, messUserDao)
    }

    // Add other repositories similarly
}