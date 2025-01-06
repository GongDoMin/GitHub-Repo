package com.prac.domain.di

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.AuthorizeOAuthUseCase
import com.prac.domain.ClearLocalDataUseCase
import com.prac.domain.GetRepositoriesUseCase
import com.prac.domain.impl.AuthorizeOAuthUseCaseImpl
import com.prac.domain.impl.ClearLocalDataUseCaseImpl
import com.prac.domain.impl.GetRepositoriesUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    @Singleton
    fun provideAuthorizeOAuthUseCase(
        tokenRepository: TokenRepository,
        userRepository: UserRepository
    ) : AuthorizeOAuthUseCase =
        AuthorizeOAuthUseCaseImpl(tokenRepository, userRepository)

    @Provides
    @Singleton
    fun provideClearLocalDataUseCase(
        tokenRepository: TokenRepository,
        userRepository: UserRepository,
        repoRepository: RepoRepository
    ) : ClearLocalDataUseCase =
        ClearLocalDataUseCaseImpl(tokenRepository, userRepository, repoRepository)

    @Provides
    @Singleton
    fun provideGetRepositoriesUseCase(
        repoRepository: RepoRepository,
        userRepository: UserRepository
    ) : GetRepositoriesUseCase =
        GetRepositoriesUseCaseImpl(repoRepository, userRepository)
}