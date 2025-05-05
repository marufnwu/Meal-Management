package com.logicline.mydining.di

import com.logicline.mydining.network.MyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMyApi(): MyApi {
        return MyApi.invoke() // use your static method here
    }
}