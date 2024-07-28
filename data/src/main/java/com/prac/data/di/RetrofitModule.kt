package com.prac.data.di

import com.prac.data.BuildConfig
import com.prac.data.di.annotation.AuthorizationInterceptorOkHttpClient
import com.prac.data.di.annotation.BasicOkHttpClient
import com.prac.data.di.annotation.GitHubRetrofit
import com.prac.data.di.annotation.BasicRetrofit
import com.prac.data.source.api.GitHubApi
import com.prac.data.source.api.GitHubTokenApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RetrofitModule {
    @Provides
    @Singleton
    @BasicRetrofit
    fun provideGitHubTokenRetrofit(
        @BasicOkHttpClient okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()

    @Provides
    @Singleton
    @GitHubRetrofit
    fun provideGitHubRetrofit(
        @AuthorizationInterceptorOkHttpClient okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()

    @Provides
    @Singleton
    fun provideGitHubTokenService(
        @BasicRetrofit retrofit: Retrofit
    ): GitHubTokenApi =
        retrofit.create(GitHubTokenApi::class.java)

    @Provides
    @Singleton
    fun provideGitHubService(
        @GitHubRetrofit retrofit: Retrofit
    ): GitHubApi =
        retrofit.create(GitHubApi::class.java)
}