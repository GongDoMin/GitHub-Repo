package com.prac.data.source.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.prac.data.BuildConfig
import com.prac.data.source.network.di.annotation.AuthOkHttpClient
import com.prac.data.source.network.di.annotation.BasicOkHttpClient
import com.prac.data.source.network.di.annotation.GitHubAuthRetrofit
import com.prac.data.source.network.di.annotation.GitHubRetrofit
import com.prac.data.source.network.di.annotation.GitHubUserRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RetrofitModule {
    @Provides
    @Singleton
    @GitHubAuthRetrofit
    fun provideGitHubAuthRetrofit(
        @BasicOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @GitHubRetrofit
    fun provideGitHubRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()

    @Provides
    @Singleton
    @GitHubUserRetrofit
    fun provideUserRetrofit(
        @BasicOkHttpClient okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
}