package com.prac.data.source.network.di

import com.prac.data.source.network.AuthApiDataSource
import com.prac.data.source.network.RepoApiDataSource
import com.prac.data.source.network.RepoStarApiDataSource
import com.prac.data.source.network.UserApiDataSource
import com.prac.data.source.network.impl.AuthApiDataSourceImpl
import com.prac.data.source.network.impl.RepoApiDataSourceImpl
import com.prac.data.source.network.impl.RepoStarApiDataSourceImpl
import com.prac.data.source.network.impl.UserApiDataSourceImpl
import com.prac.data.source.network.service.GitHubAuthService
import com.prac.data.source.network.service.GitHubService
import com.prac.data.source.network.service.GitHubUserService
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