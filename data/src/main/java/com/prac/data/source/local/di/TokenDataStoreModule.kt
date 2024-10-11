package com.prac.data.source.local.di

import android.content.Context
import com.prac.data.source.local.datastore.TokenDataStoreManager
import com.prac.data.source.local.datastore.TokenDataStoreManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class TokenDataStoreModule {
    @Provides
    @Singleton
    fun provideTokenDataStoreManager(@ApplicationContext context: Context) : TokenDataStoreManager {
        return TokenDataStoreManagerImpl(context)
    }
}