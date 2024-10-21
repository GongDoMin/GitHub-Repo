package com.prac.network.di

import com.prac.local.TokenLocalDataSource
import com.prac.network.AuthApiDataSource
import com.prac.network.BuildConfig
import com.prac.network.di.annotation.AuthOkHttpClient
import com.prac.network.di.annotation.BasicOkHttpClient
import com.prac.network.service.AuthorizationInterceptor
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
internal class OkHttpClientModule {
    @Provides
    fun provideAuthorizationInterceptor(
        authApiDataSource: AuthApiDataSource,
        tokenLocalDataSource: TokenLocalDataSource
    ) : Interceptor =
        AuthorizationInterceptor(authApiDataSource, tokenLocalDataSource)

    @Provides
    @Singleton
    @BasicOkHttpClient
    fun provideBasicOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(timeout = 10, unit = TimeUnit.SECONDS)
            .readTimeout(timeout = 10, unit = TimeUnit.SECONDS)
            .writeTimeout(timeout = 10, unit = TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()

    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthorizationOkHttpClient(authorizationInterceptor: AuthorizationInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(timeout = 10, unit = TimeUnit.SECONDS)
            .readTimeout(timeout = 10, unit = TimeUnit.SECONDS)
            .writeTimeout(timeout = 10, unit = TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
                }
            )
            .addInterceptor(authorizationInterceptor)
            .build()
}