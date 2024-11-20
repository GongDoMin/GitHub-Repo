package com.prac.githubrepo.di

import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.ui.login.LoginActionProcessor
import com.prac.githubrepo.ui.login.UserActionProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ActionProcessorModule {
    @Provides
    fun providesLoginActionProcessor(tokenRepository: TokenRepository) =
        LoginActionProcessor(tokenRepository)

    @Provides
    fun providesUserActionProcessor() =
        UserActionProcessor()
}