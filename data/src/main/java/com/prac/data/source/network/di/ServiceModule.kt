package com.prac.data.source.network.di

import com.prac.data.source.network.di.annotation.GitHubAuthRetrofit
import com.prac.data.source.network.di.annotation.GitHubRetrofit
import com.prac.data.source.network.di.annotation.GitHubUserRetrofit
import com.prac.data.source.network.service.GitHubAuthService
import com.prac.data.source.network.service.GitHubService
import com.prac.data.source.network.service.GitHubUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ServiceModule {
    @Provides
    @Singleton
    fun provideGitHubAuthService(
        @GitHubAuthRetrofit retrofit: Retrofit
    ): GitHubAuthService =
        retrofit.create(GitHubAuthService::class.java)

    @Provides
    @Singleton
    fun provideGitHubService(
        @GitHubRetrofit retrofit: Retrofit
    ): GitHubService =
        retrofit.create(GitHubService::class.java)

    @Provides
    @Singleton
    fun provideGitHubUserService(
        @GitHubUserRetrofit retrofit: Retrofit
    ): GitHubUserService =
        retrofit.create(GitHubUserService::class.java)
}