package com.prac.network.di

import com.prac.network.AuthApiDataSource
import com.prac.network.RepoApiDataSource
import com.prac.network.RepoStarApiDataSource
import com.prac.network.UserApiDataSource
import com.prac.network.impl.AuthApiDataSourceImpl
import com.prac.network.impl.RepoApiDataSourceImpl
import com.prac.network.impl.RepoStarApiDataSourceImpl
import com.prac.network.impl.UserApiDataSourceImpl
import com.prac.network.service.GitHubAuthService
import com.prac.network.service.GitHubService
import com.prac.network.service.GitHubUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class DataSourceModule {
    @Provides
    fun provideAuthApiDataSource(
        gitHubAuthService: GitHubAuthService
    ): AuthApiDataSource =
        AuthApiDataSourceImpl(gitHubAuthService)

    @Provides
    fun provideRepoApiDataSource(
        gitHubService: GitHubService
    ): RepoApiDataSource =
        RepoApiDataSourceImpl(gitHubService)

    @Provides
    fun provideRepoStarApiDataSource(
        gitHubService: GitHubService
    ): RepoStarApiDataSource =
        RepoStarApiDataSourceImpl(gitHubService)

    @Provides
    fun provideUserApiDataSource(
        gitHubUserService: GitHubUserService
    ): UserApiDataSource =
        UserApiDataSourceImpl(gitHubUserService)
}