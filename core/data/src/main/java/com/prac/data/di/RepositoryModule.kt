package com.prac.data.di

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.impl.RepoRepositoryImpl
import com.prac.data.impl.TokenRepositoryImpl
import com.prac.data.impl.UserRepositoryImpl
import com.prac.data.repository.UserRepository
import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.TokenLocalDataSource
import com.prac.local.UserLocalDataSource
import com.prac.local.room.database.RepositoryDatabase
import com.prac.network.AuthApiDataSource
import com.prac.network.RepoApiDataSource
import com.prac.network.RepoStarApiDataSource
import com.prac.network.UserApiDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideTokenRepository(
        tokenLocalDataSource: TokenLocalDataSource,
        authApiDataSource: AuthApiDataSource
    ): TokenRepository =
        TokenRepositoryImpl(tokenLocalDataSource, authApiDataSource)

    @Provides
    @Singleton
    fun provideRepoRepository(
        repoApiDataSource: RepoApiDataSource,
        repoStarApiDataSource: RepoStarApiDataSource,
        repositoryLocalDataSource: RepositoryLocalDataSource,
        remoteKeyLocalDataSource: RemoteKeyLocalDataSource,
        userLocalDataSource: UserLocalDataSource
    ): RepoRepository =
        RepoRepositoryImpl(repoApiDataSource, repoStarApiDataSource, repositoryLocalDataSource, remoteKeyLocalDataSource, userLocalDataSource)

    @Provides
    @Singleton
    fun provideUserRepository(
        userApiDataSource: UserApiDataSource,
        userLocalDataSource: UserLocalDataSource
    ): UserRepository =
        UserRepositoryImpl(userApiDataSource, userLocalDataSource)
}