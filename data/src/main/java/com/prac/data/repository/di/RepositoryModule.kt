package com.prac.data.repository.di

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.impl.RepoRepositoryImpl
import com.prac.data.repository.impl.TokenRepositoryImpl
import com.prac.data.source.local.TokenLocalDataSource
import com.prac.data.source.local.UserLocalDataSource
import com.prac.data.source.local.room.database.RepositoryDatabase
import com.prac.data.source.network.AuthApiDataSource
import com.prac.data.source.network.RepoApiDataSource
import com.prac.data.source.network.RepoStarApiDataSource
import com.prac.data.source.network.UserApiDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {
    @Provides
    @Singleton
    fun provideTokenRepository(
        tokenLocalDataSource: TokenLocalDataSource,
        authApiDataSource: AuthApiDataSource,
        userApiDataSource: UserApiDataSource,
        userLocalDataSource: UserLocalDataSource
    ): TokenRepository =
        TokenRepositoryImpl(tokenLocalDataSource, authApiDataSource, userApiDataSource, userLocalDataSource)

    @Provides
    @Singleton
    fun provideRepoRepository(
        repoApiDataSource: RepoApiDataSource,
        repoStarApiDataSource: RepoStarApiDataSource,
        repositoryDatabase: RepositoryDatabase,
        userLocalDataSource: UserLocalDataSource
    ): RepoRepository =
        RepoRepositoryImpl(repoApiDataSource, repoStarApiDataSource, repositoryDatabase, userLocalDataSource)
}