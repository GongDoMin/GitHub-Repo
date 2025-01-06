package com.prac.auth.di

import com.prac.auth.AuthManager
import com.prac.auth.impl.BearerAuthImpl
import com.prac.local.TokenLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class AuthManagerModule {
    @Provides
    fun provideAuthManager(
        tokenLocalDataSource: TokenLocalDataSource
    ) : AuthManager =
        BearerAuthImpl(tokenLocalDataSource)
}