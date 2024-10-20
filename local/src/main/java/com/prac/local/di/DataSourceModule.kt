package com.prac.local.di

import com.prac.local.TokenLocalDataSource
import com.prac.local.UserLocalDataSource
import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.datastore.user.UserDataStoreManager
import com.prac.local.impl.TokenLocalDataSourceImpl
import com.prac.local.impl.UserLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class DataSourceModule {
    @Provides
    fun provideTokenLocalDataSource(
        tokenDataStoreManager: TokenDataStoreManager
    ): TokenLocalDataSource =
        TokenLocalDataSourceImpl(tokenDataStoreManager)

    @Provides
    fun provideUserLocalDataSource(
        userDataStoreManager: UserDataStoreManager
    ): UserLocalDataSource =
        UserLocalDataSourceImpl(userDataStoreManager)
}