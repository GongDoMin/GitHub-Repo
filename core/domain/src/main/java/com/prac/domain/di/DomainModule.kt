package com.prac.domain.di

import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.AuthorizeOAuthUseCase
import com.prac.domain.IsStarredUseCase
import com.prac.domain.impl.AuthorizeOAuthUseCaseImpl
import com.prac.domain.impl.IsStarredUseCaseImpl
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
    fun provideClearTokenUseCase(
        tokenRepository: TokenRepository,
        userRepository: UserRepository
    ) : AuthorizeOAuthUseCase =
        AuthorizeOAuthUseCaseImpl(tokenRepository, userRepository)

    @Provides
    @Singleton
    fun provideIsStarredUseCase(
        repoRepository: RepoRepository,
        userRepository: UserRepository
    ) : IsStarredUseCase =
        IsStarredUseCaseImpl(repoRepository, userRepository)
}