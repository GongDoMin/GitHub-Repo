package com.prac.data.di

import com.prac.data.BuildConfig
import com.prac.data.di.annotation.BasicOkHttpClient
import com.prac.data.di.binds.TokenSharedPreferences
import com.prac.data.source.api.AuthorizationInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {
    @Provides
    fun provideAuthorizationInterceptor(tokenSharedPreferences: TokenSharedPreferences) : Interceptor =
        AuthorizationInterceptor(tokenSharedPreferences)

    @Provides
    @Singleton
    @BasicOkHttpClient
    fun provideBasicOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(timeout = 5, unit = TimeUnit.SECONDS)
            .readTimeout(timeout = 5, unit = TimeUnit.SECONDS)
            .writeTimeout(timeout = 5, unit = TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
}