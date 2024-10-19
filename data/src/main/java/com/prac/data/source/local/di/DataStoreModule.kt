package com.prac.data.source.local.di

import android.content.Context
import com.prac.data.source.local.datastore.token.TokenDataStoreManager
import com.prac.data.source.local.datastore.token.TokenDataStoreManagerImpl
import com.prac.data.source.local.datastore.user.UserDataStoreManager
import com.prac.data.source.local.datastore.user.UserDataStoreManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataStoreModule {
    @Provides
    @Singleton
    fun provideTokenDataStoreManager(@ApplicationContext context: Context) : TokenDataStoreManager {
        return TokenDataStoreManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideUserDataStoreManager(@ApplicationContext context: Context) : UserDataStoreManager {
        return UserDataStoreManagerImpl(context)
    }
}