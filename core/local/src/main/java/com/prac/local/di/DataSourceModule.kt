package com.prac.local.di

import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.TokenLocalDataSource
import com.prac.local.UserLocalDataSource
import com.prac.local.datastore.token.TokenDataStoreManager
import com.prac.local.datastore.user.UserDataStoreManager
import com.prac.local.impl.RemoteKeyLocalDataSourceImpl
import com.prac.local.impl.RepositoryLocalDataSourceImpl
import com.prac.local.impl.TokenLocalDataSourceImpl
import com.prac.local.impl.UserLocalDataSourceImpl
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.dao.RepositoryDao
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

    @Provides
    fun provideRepositoryLocalDataSource(
        repositoryDao: RepositoryDao
    ): RepositoryLocalDataSource =
        RepositoryLocalDataSourceImpl(repositoryDao)

    @Provides
    fun provideRemoteKeyLocalDataSource(
        remoteKeyDao: RemoteKeyDao
    ): RemoteKeyLocalDataSource =
        RemoteKeyLocalDataSourceImpl(remoteKeyDao)
}